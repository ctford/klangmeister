(ns leipzig-live.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [cljs.js :as cljs]))

;; -------------------------
;; Model
(defonce music (atom "7"))

;; -------------------------
;; Behaviour
(defonce compiler-state (cljs/empty-state))
(defn evaluate
  [expr-str]
  (cljs/eval-str
    compiler-state
    expr-str
    nil
    {:eval cljs/js-eval}
    #(print-str expr-str " evaluates to " %)))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h1 "Welcome to Leipzig Live!"]
   [:div [:input {:type "text"
                  :value (-> music deref print-str)
                  :on-change #(reset! music (-> % .-target .-value))}]]
   [:div
    (evaluate @music)]])

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
