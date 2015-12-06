(ns leipzig-live.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [cljs.js :as cljs]))

;; -------------------------
;; Model
(defonce state
  (atom {:music ""
         :playing nil}))

;; -------------------------
;; Behaviour
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

(defonce context (js/window.AudioContext.))
(defn beep []
  (let [oscillator (.createOscillator context)]
    (doto oscillator
      (.connect (.-destination context))
      (-> .-frequency .-value (set! 440))
      (-> .-type (set! "square"))
      (.start 0))
    (swap! state assoc-in [:playing] oscillator)))

(defn kill []
  (.stop (:playing @state) 0)
  (swap! state assoc-in [:playing] nil))

(defn handle [expr-str]
  (swap! state assoc-in [:music] expr-str))

(defn toggle []
  (if (:playing @state)
    (kill)
    (beep)))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h1 "Welcome to Leipzig Live!"]
   [:div [:input {:type "text"
                  :value (-> state deref :music print-str)
                  :on-change #(-> % .-target .-value handle)}]
    [:button {:on-click toggle} "Start/stop"]]
   [:div
    (evaluate (-> @state :music))]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
