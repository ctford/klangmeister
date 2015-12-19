(ns leipzig-live.core
  (:require
    [leipzig-live.music :as music]
    [leipzig-live.views :as view]
    [leipzig-live.instruments :as instrument]
    [leipzig-live.actions :as action]
    [leipzig-live.framework :as framework]
    [reagent.core :as reagent :refer [atom]]
    [cljs.js :as cljs]))

;; -------------------------
;; Evaluation

(defonce compiler-state (cljs/empty-state))
(defn evaluate
  [expr-str]
  (cljs/eval-str
    compiler-state
    (str "(identity " expr-str ")")
    nil
    {:eval cljs/js-eval}
    #(:value %)))

;; -------------------------
;; Behaviour

(extend-protocol framework/Action
  action/Refresh
  (process [{expr-str :text} state]
    (let [new-state (assoc-in state [:text] expr-str)]
      (if-let [value (evaluate expr-str)]
        (assoc-in new-state [:music] value)
        new-state)))

  action/Play
  (process [_ {pitches :music :as original-state}]
    (music/play-on! instrument/beep! (repeat 1) pitches)
    original-state))

;; -------------------------
;; Wiring

(defn mount-root []
  (let [state-atom (atom {:music [100 120]
                          :text "'(100 120)"})]
    (reagent/render
      [view/home-page (partial framework/apply-action! state-atom) @state-atom]
      js/document.body)))

(defn init! []
  (mount-root))
