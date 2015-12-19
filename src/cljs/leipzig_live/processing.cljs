(ns leipzig-live.processing
  (:require
    [leipzig-live.music :as music]
    [leipzig-live.instruments :as instrument]
    [leipzig-live.actions :as action]
    [leipzig-live.framework :as framework]
    [cljs.js :as cljs]))

(defonce compiler-state (cljs/empty-state))
(defn evaluate
  [expr-str]
  (cljs/eval-str
    compiler-state
    (str "(identity " expr-str ")")
    nil
    {:eval cljs/js-eval}
    #(:value %)))

(extend-protocol framework/Action
  action/Refresh
  (process [{expr-str :text} state]
    (let [new-state (assoc-in state [:text] expr-str)]
      (if-let [value (evaluate expr-str)]
        (assoc-in new-state [:music] value)
        new-state)))

  action/Play
  (process [_ {[durations pitches] :music :as original-state}]
    (music/play-on! instrument/beep! durations pitches)
    original-state)

  action/Loop
  (process [_ {[durations pitches] :music :as original-state}]
    (let [once #(music/play-on! instrument/beep! durations pitches)]
      (once)
      (js/setTimeout once (* 1000 (reduce + durations))))
    original-state))
