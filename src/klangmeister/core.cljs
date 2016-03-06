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
    {:audiocontext (if js/window.AudioContext.
                     (js/window.AudioContext.)
                     (js/window.webkitAudioContext.))
     :main nothing
     :synth nothing}))

(defonce state-atom (empty-state))

(def handle! (framework/handler-for state-atom))

(defn reload! []
  (swap! state-atom identity))

(secretary/defroute "/klangmeister/" [query-params]
  (session/put! :gist (:gist query-params))
  (session/put! :current-page view/about))

(secretary/defroute "/klangmeister/index.html" [query-params]
  (session/put! :gist (:gist query-params))
  (session/put! :current-page view/about))

(secretary/defroute "/klangmeister/synthesis" []
  (session/put! :current-page view/synthesis))

(secretary/defroute "/klangmeister/performance" []
  (session/put! :current-page view/performance))

(secretary/defroute "/klangmeister/composition" []
  (session/put! :current-page view/composition))

(secretary/defroute "/klangmeister/reference" []
  (session/put! :current-page view/reference))

(secretary/defroute "/klangmeister/about" []
  (session/put! :current-page view/about))

(defn current-page []
  [(session/get :current-page) handle! state-atom])

(defn mount-root []
  (let [default (or (session/get :gist) "4b04fd7f2d361c6604c4")]
    (handle! (action/->Import default :main))
    (reagent/render
      [current-page]
      js/document.body)))

(defn main []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))

(main)
