(ns klangmeister.processing
  (:require
    [klangmeister.compile.eval :as eval]
    [klangmeister.ui.reference :as reference]
    [klangmeister.sound.music :as music]
    [klangmeister.sound.instruments :as instrument]
    [klangmeister.actions :as action]
    [klangmeister.framework :as framework]
    [ajax.core :as ajax]
    [leipzig.melody :as melody]))

(defn milli [n]
  (* n 1000))

(defn schedule! [callback! duration]
  (let [margin 150]
    (js/setTimeout callback! (- (milli duration) margin))))

(defn clear-syncs [state pane]
  (-> state
      (update-in [pane :sync] dissoc)
      (update-in [pane :audio-sync] dissoc)))

(defn update-syncs [state pane audio-sync duration]
  (-> state
      (assoc-in [pane :sync] (+ (Date.now) (milli duration)))
      (assoc-in [pane :audio-sync] (+ audio-sync duration))))

(defn too-many? [value]
  (when (and (seq? value) (->> value (drop 1000) first))
    "Too many notes - Klangmeister can't handle more than 1000."))

(defn well-formed? [value]
  (letfn [(ok? [{:keys [time duration]}] (and time duration))]
    (when (and (seq? value) (not-every? ok? value))
      "All notes must have a time and a duration.")))

(defn check [{:keys [value error] :as return} ok?]
  (if error
    return
    (assoc return :error (ok? value))))

(defn refresh [{expr-str :text pane :target} _ state]
  (let [{:keys [value error]} (-> expr-str
                                  eval/uate
                                  (check too-many?)
                                  (check well-formed?))]
    (if error
      (-> state
          (assoc-in [pane :error] error)
          (assoc-in [pane :text] expr-str))
      (-> state
          (assoc-in [pane :error] nil)
          (assoc-in [pane :value] value)
          (assoc-in [pane :text] expr-str)))))

(def gist-uri (partial str "https://api.github.com/gists/"))

(extend-protocol framework/Action
  action/Refresh
  (process [this handle! state]
    (refresh this handle! state))

  action/Gist
  (process [{gist :gist pane :target} handle! state]
    (let [refresh #(handle! (action/->Refresh % pane))
          handler #(-> % :files vals first :content refresh)]
      (ajax/GET (gist-uri gist) {:handler handler :response-format :json :keywords? true})
      state))

  action/Import
  (process [{uri :uri pane :target} handle! state]
    (let [refresh #(handle! (action/->Refresh % pane))]
      (ajax/GET uri {:handler refresh})
      state))

  action/Stop
  (process [{pane :target} handle! state]
    (assoc-in state [pane :looping?] false))

  action/Play
  (process [{pane :target :as this} handle! state]
    (if-not (< (Date.now) (get-in state [pane :sync]))
      (framework/process (action/->Loop pane) handle! (assoc-in state [pane :looping?] true))
      state))

  action/PlayOnce
  (process [{pane :target :as this} handle! {:keys [audiocontext] :as state}]
    (let [{:keys [value]} (pane state)]
      (->> value
           (melody/wherever (comp not :instrument), :instrument (melody/is instrument/bell))
           (music/play! audiocontext))
      state))

  action/Doc
  (process [{pane :target doc :string} handle! state]
    (-> state
        (assoc-in [pane :doc]
                  (when-let [documentation (reference/all doc)]
                    (cons doc documentation)))))

  action/Test
  (process [{pane :target :as this} handle! {:keys [audiocontext] :as state}]
    (let [{:keys [value]} (pane state)]
      (music/play! audiocontext [{:time 0 :duration 1 :instrument (constantly value)}])
      state))

  action/Loop
  (process [{pane :target :as this} handle! {:keys [audiocontext] :as state}]
    (let [{:keys [value looping? audio-sync] :or {audio-sync (.-currentTime audiocontext)}} (pane state)
          duration (melody/duration value)]
      (if looping?
        (do (music/play-from! audiocontext audio-sync value)
            (schedule! #(handle! this) duration)
            (-> state (update-syncs pane audio-sync duration)))
        (-> state (clear-syncs pane))))))
