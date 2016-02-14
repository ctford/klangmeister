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

(defn synthesis-page []
  (let [handle! (framework/handler-for state-atom)]
    [view/content handle! state-atom]))

(defn performance-page []
  (let [handle! (framework/handler-for state-atom)]
    [view/performance handle! state-atom]))

(defn about-page []
  [view/about])

(def home-page about-page)

(defn music-page []
  (let [handle! (framework/handler-for state-atom)]
    [view/music handle! state-atom]))

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/synthesis" []
  (session/put! :current-page #'synthesis-page))

(secretary/defroute "/performance" []
  (session/put! :current-page #'performance-page))

(secretary/defroute "/music" []
  (session/put! :current-page #'music-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

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
