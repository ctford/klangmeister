(ns leipzig-live.core
    (:require [reagent.core :as reagent :refer [atom]]
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

(defn identify
  "Hack to make literal values still evaluate."
  [expr-str]
  (str "(identity " expr-str ")"))

(defonce compiler-state (cljs/empty-state))
(defn evaluate
  [expr-str]
    (cljs/eval-str
      compiler-state
      (identify expr-str)
      nil
      {:eval cljs/js-eval}
      #(:value %)))

;; -------------------------
;; Behaviour

(defprotocol Action
  (process [this state]))

(defrecord Play [])
(defrecord Refresh [text])

(defn note [t p] {:time t :pitch p})
(extend-protocol Action
  Refresh
  (process [{expr-str :text} state]
    (let [new-state (assoc-in state [:text] expr-str)]
      (if-let [value (evaluate expr-str)]
        (assoc-in new-state [:music] value)
        new-state)))

  Play
  (process [_ state]
    (doseq [{seconds :time hertz :pitch} (map note (range) (:music state))]
      (beep! hertz seconds 1))
    state))

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
;; Initialize app

(defn mount-root []
  (let [state-atom (atom {:music [100 120]
                     :text "'(100 120)"})]
    (reagent/render
      [home-page (partial apply-action! state-atom) @state-atom]
      js/document.body)))

(defn init! []
  (mount-root))
