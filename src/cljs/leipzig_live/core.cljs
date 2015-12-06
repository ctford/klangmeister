(ns leipzig-live.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [cljs.js :as cljs]))

;; -------------------------
;; Model
(defonce state
  (atom {:music nil
         :text ""
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
(defn beep [freq]
    (doto (.createOscillator context)
      (.connect (.-destination context))
      (-> .-frequency .-value (set! freq))
      (-> .-type (set! "square"))
      (.start 0)))

(defn kill [oscillator]
  (.stop oscillator 0))

(defn handle [expr-str]
  (swap! state assoc-in [:text] expr-str)
  (when-let [value (evaluate expr-str)]
    (swap! state assoc-in [:music] value)))

(defn toggle []
  (if (:playing @state)
    (let [oscillator (:playing @state)]
      (kill oscillator)
      (swap! state assoc-in [:playing] nil))
    (let [oscillator (beep (:music @state))]
      (swap! state assoc-in [:playing] oscillator))))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h1 "Welcome to Leipzig Live!"]
   [:div [:input {:type "text"
                  :value (:text @state)
                  :on-change #(-> % .-target .-value handle)}]
    [:button {:on-click toggle} "Start/stop"]]
   [:div
    (:music @state)]])

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
