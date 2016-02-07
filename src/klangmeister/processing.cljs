(ns klangmeister.processing
  (:require
    [klangmeister.eval :as eval]
    [klangmeister.music :as music]
    [klangmeister.actions :as action]
    [klangmeister.framework :as framework]
    [leipzig.melody :as melody]))

(extend-protocol framework/Action
  action/Refresh
  (process [{expr-str :text pane :target} _ state]
    (let [{:keys [value error]} (eval/uate expr-str)
          music (or value (get-in state [pane :music]) )]
      (-> state
          (assoc-in [pane :error] error)
          (assoc-in [pane :text] expr-str)
          (assoc-in [pane :music] music))))

  action/Stop
  (process [{pane :target} handle! state]
    (assoc-in state [pane :looping?] false))

  action/Play
  (process [{pane :target :as this} handle! state]
    (framework/process (action/->Loop pane) handle! (assoc-in state [pane :looping?] true)))

  action/PlayOnce
  (process [{pane :target :as this} handle! state]
    (let [{:keys [music]} (pane state)]
      (music/play-on! music)
      state))

  action/Loop
  (process [{pane :target :as this} handle! state]
    (let [{:keys [music looping?]} (pane state)
          start (Date.now)]
      (if looping?
        (do (music/play-on! music)
            (js/setTimeout #(handle! this) (* 1000 (melody/duration music)))
            (assoc-in state [pane :sync] start))
        (assoc-in state [pane :sync] nil)))))
