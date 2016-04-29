(ns klangmeister.core
  (:require
    [klangmeister.processing] ; Import action defs.
    [klangmeister.actions :as action]
    [klangmeister.ui.view :as view]
    [klangmeister.framework :as framework]
    [reagent.core :as reagent]
    [reagent.session :as session]
    [cljs-bach.synthesis :as synthesis]
    [accountant.core :as accountant]
    [secretary.core :as secretary :include-macros true]))

(defonce state-atom (reagent/atom {:audiocontext (synthesis/audio-context)}))

(secretary/defroute "/klangmeister/"            [query-params] (session/put! :gist (:gist query-params))
                                                               (session/put! :uri (:uri query-params))
                                                               (session/put! :current-page view/about))

(secretary/defroute "/klangmeister/index.html"  [query-params] (session/put! :gist (:gist query-params))
                                                               (session/put! :uri (:uri query-params))
                                                               (session/put! :current-page view/about))

(secretary/defroute "/klangmeister/synthesis"   [] (session/put! :current-page view/synthesis))
(secretary/defroute "/klangmeister/performance" [] (session/put! :current-page view/performance))
(secretary/defroute "/klangmeister/composition" [] (session/put! :current-page view/composition))
(secretary/defroute "/klangmeister/reference"   [] (session/put! :current-page view/reference))
(secretary/defroute "/klangmeister/about"       [] (session/put! :current-page view/about))

(def handle!
  "An handler that components can use to raise events."
  (framework/handler-for state-atom))

(defn current-page
  "Extract the current page from the session and use it to build the page."
  []
  [(session/get :current-page) handle! state-atom])

(defn mount-root []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (let [user-specified-uri (session/get :uri)
        user-specified-gist (session/get :gist)
        gist (or user-specified-gist "4b04fd7f2d361c6604c4")]
    (if (or user-specified-uri user-specified-gist)
      (accountant/navigate! "/klangmeister/performance"))
    (handle!
      ; Pull in the content of the main code pane.
      (if user-specified-uri
        (action/->Import user-specified-uri :main)
        (action/->Gist gist :main)))
    (reagent/render [current-page] js/document.body)))

(mount-root)
