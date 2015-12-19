(ns leipzig-live.core
  (:require
    [leipzig-live.music :as music]
    [leipzig-live.actions :as action]
    [reagent.core :as reagent :refer [atom]]
    [cljs.js :as cljs]))

;; -------------------------
;; Sound

(defonce context (js/window.AudioContext.))
(defn beep! [freq start dur]
  (let [start (+ start (.-currentTime context))
        stop (+ start dur)]
    (doto (.createOscillator context)
      (.connect (.-destination context))
      (-> .-frequency .-value (set! freq))
      (-> .-type (set! "square"))
      (.start start)
      (.stop stop))))

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

(defprotocol Action
  (process [this state]))


(extend-protocol Action
  actions/Refresh
  (process [{expr-str :text} state]
    (let [new-state (assoc-in state [:text] expr-str)]
      (if-let [value (evaluate expr-str)]
        (assoc-in new-state [:music] value)
        new-state)))

  actions/Play
  (process [_ {pitches :music :as original-state}]
    (music/play-on! beep! (repeat 1) pitches)
    original-state))

(defn apply-action! [state-atom action]
  (swap! state-atom (partial process action)))

;; -------------------------
;; Views

(defn home-page [handle! state]
  [:div [:h1 "Welcome to Leipzig Live!"]
   [:div [:input {:type "text"
                  :value (:text state)
                  :on-change #(-> % .-target .-value ->Refresh handle!)}]
   [:button {:on-click (fn [_] (handle! (->Play)))} "Play!"]]
   [:div
    (-> state :music print)]])

;; -------------------------
;; Wiring

(defn mount-root []
  (let [state-atom (atom {:music [100 120]
                          :text "'(100 120)"})]
    (reagent/render
      [home-page (partial apply-action! state-atom) @state-atom]
      js/document.body)))

(defn init! []
  (mount-root))
