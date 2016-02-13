(ns klangmeister.processing
  (:require
    [klangmeister.compile.eval :as eval]
    [klangmeister.sound.music :as music]
    [klangmeister.actions :as action]
    [klangmeister.framework :as framework]
    [leipzig.melody :as melody]))

(extend-protocol framework/Action
  action/Refresh
  (process [{expr-str :text pane :target} _ state]
    (let [{:keys [value error]} (eval/uate expr-str)
          value (or value (get-in state [pane :value]) )]
      (-> state
          (assoc-in [pane :error] error)
          (assoc-in [pane :text] expr-str)
          (assoc-in [pane :value] value))))

  action/Stop
  (process [{pane :target} handle! state]
    (assoc-in state [pane :looping?] false))

  action/Play
  (process [{pane :target :as this} handle! state]
    (framework/process (action/->Loop pane) handle! (assoc-in state [pane :looping?] true)))

  action/PlayOnce
  (process [{pane :target :as this} handle! state]
    (let [{:keys [value]} (pane state)]
      (music/play! value)
      state))

  action/Test
  (process [{pane :target :as this} handle! state]
    (let [{:keys [value]} (pane state)]
      (music/play! [{:time 0 :duration 1 :instrument (constantly value)}])
      state))

  action/Loop
  (process [{pane :target :as this} handle! state]
    (let [{:keys [value looping?]} (pane state)
          start (Date.now)]
      (if looping?
        (do (music/play! value)
            (js/setTimeout #(handle! this) (* 1000 (melody/duration value)))
            (assoc-in state [pane :sync] start))
        (assoc-in state [pane :sync] nil)))))
