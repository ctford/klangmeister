(ns klangmeister.processing
  (:require
    [klangmeister.compile.eval :as eval]
    [klangmeister.sound.music :as music]
    [klangmeister.sound.instruments :as instrument]
    [klangmeister.actions :as action]
    [klangmeister.framework :as framework]
    [ajax.core :as ajax]
    [leipzig.melody :as melody]))

(defn safety-cutoff
  "Cutoff evaluation to prevent infinite seqs breaking everything."
  [notes]
  (if (seq? notes)
    (let [max-notes 1000]
      (->> notes (take max-notes)))
    notes))

(extend-protocol framework/Action
  action/Refresh
  (process [{expr-str :text pane :target} _ state]
    (let [{:keys [value error]} (eval/uate expr-str)
          value (some-> value safety-cutoff)]
      (-> state
          (assoc-in [pane :error] error)
          (assoc-in [pane :text] expr-str)
          (update-in [pane :value] #(or value %)))))

  action/Import
  (process [{gist :gist pane :target} handle! state]
    (let [refresh #(handle! (action/->Refresh % pane))
          handler #(-> % :files vals first :content refresh)
          uri (str "https://api.github.com/gists/" gist)]
      (ajax/GET uri {:handler handler :response-format :json :keywords? true})
      state))

  action/Stop
  (process [{pane :target} handle! state]
    (assoc-in state [pane :looping?] false))

  action/Play
  (process [{pane :target :as this} handle! state]
    (framework/process (action/->Loop pane) handle! (assoc-in state [pane :looping?] true)))

  action/PlayOnce
  (process [{pane :target :as this} handle! {:keys [audiocontext] :as state}]
    (let [{:keys [value]} (pane state)]
      (->> value
           (melody/wherever (comp not :instrument), :instrument (melody/is instrument/bell))
           (music/play! audiocontext))
      state))

  action/Test
  (process [{pane :target :as this} handle! {:keys [audiocontext] :as state}]
    (let [{:keys [value]} (pane state)]
      (music/play! audiocontext [{:time 0 :duration 1 :instrument (constantly value)}])
      state))

  action/Loop
  (process [{pane :target :as this} handle! {:keys [audiocontext] :as state}]
    (let [{:keys [value looping?]} (pane state)
          start (Date.now)]
      (if looping?
        (do (music/play! audiocontext value)
            (js/setTimeout #(handle! this) (* 1000 (melody/duration value)))
            (assoc-in state [pane :sync] start))
        (assoc-in state [pane :sync] nil)))))
