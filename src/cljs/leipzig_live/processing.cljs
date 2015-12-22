(ns leipzig-live.processing
  (:require
    [leipzig-live.music :as music]
    [leipzig-live.instruments :as instrument]
    [leipzig-live.actions :as action]
    [leipzig-live.framework :as framework]
    [cljs.js :as cljs]))

(defn evaluate
  [expr-str]
  (cljs/eval-str
    (cljs/empty-state)
    expr-str
    nil
    {:eval cljs/js-eval}
    #(:value %)))

(extend-protocol framework/Action
  action/Refresh
  (process [{expr-str :text} _ {original-music :music :as state}]
    (let [music (or (evaluate expr-str) original-music)]
      (-> state
          (assoc :text expr-str)
          (assoc :music music))))

  action/Stop
  (process [_ handle! state]
    (assoc state :looping? false))

  action/Play
  (process [this handle! {[durations pitches] :music :as state}]
    (framework/process (action/->Loop) handle! (assoc state :looping? true)))

  action/Loop
  (process [this handle! {[durations pitches] :music :as state}]
    (when (:looping? state)
      (music/play-on! instrument/beep! durations pitches)
      (let [duration (* 1000 (reduce + durations))]
        (js/setTimeout #(handle! this) duration)))
    state))
