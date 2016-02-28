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
    [view/synthesis handle! state-atom]))

(defn performance-page []
  (let [handle! (framework/handler-for state-atom)]
    [view/performance handle! state-atom]))

(defn about-page []
  (let [handle! (framework/handler-for state-atom)]
    [view/about handle! state-atom]))

(defn reference-page []
  (let [handle! (framework/handler-for state-atom)]
    [view/reference handle! state-atom]))

(def home-page about-page)

(defn composition-page []
  (let [handle! (framework/handler-for state-atom)]
    [view/composition handle! state-atom]))

(secretary/defroute "/klangmeister/" [query-params]
  (session/put! :gist (:gist query-params))
  (session/put! :current-page #'home-page))

(secretary/defroute "/klangmeister/index.html" [query-params]
  (session/put! :gist (:gist query-params))
  (session/put! :current-page #'home-page))

(secretary/defroute "/klangmeister/synthesis" []
  (session/put! :current-page #'synthesis-page))

(secretary/defroute "/klangmeister/performance" []
  (session/put! :current-page #'performance-page))

(secretary/defroute "/klangmeister/composition" []
  (session/put! :current-page #'composition-page))

(secretary/defroute "/klangmeister/reference" []
  (session/put! :current-page #'reference-page))

(secretary/defroute "/klangmeister/about" []
  (session/put! :current-page #'about-page))

(defn current-page []
  [(session/get :current-page)])

(defn mount-root []
  (let [handle! (framework/handler-for state-atom)
        default (or (session/get :gist) "4b04fd7f2d361c6604c4")]
    (handle! (action/->Import default :main))
    (reagent/render
      [current-page]
      js/document.body)))

(defn main []
  (session/put! :current-page #'home-page)
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))

(main)
