(ns klangmeister.core
  (:require
    [klangmeister.processing] ; Import action defs.
    [klangmeister.actions :as action]
    [klangmeister.ui.view :as view]
    [klangmeister.framework :as framework]
    [reagent.core :as reagent]
    [reagent.session :as session]
    [accountant.core :as accountant]
    [secretary.core :as secretary :include-macros true])
  (:require-macros
    [klangmeister.compile.macros :as macro]))

(def nothing
  {:looping? false
   :error nil
   :text ""
   :value []})

(defn empty-state []
  (reagent/atom
    {:main nothing
     :synth nothing}))

(defonce state-atom (empty-state))

(defn reload! []
  (swap! state-atom identity))

(defn home-page []
  (let [handle! (framework/handler-for state-atom)]
    [view/render handle! state-atom]))

(def synthesis-page home-page)

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/synthesis" []
  (session/put! :current-page #'synthesis-page))

(defn current-page []
  [(session/get :current-page)])

(defn mount-root []
  (let [handle! (framework/handler-for state-atom)]
    (handle! (action/->Refresh (macro/text "src/klangmeister/live.cljs.txt") :main))
    (reagent/render
      [current-page]
      js/document.body)))

(defn main []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))

(main)
