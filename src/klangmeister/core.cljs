(ns klangmeister.core
  (:require
    [klangmeister.processing] ; Import action defs.
    [klangmeister.eval :as eval]
    [klangmeister.views :as view]
    [klangmeister.framework :as framework]
    [reagent.core :as reagent]))

(def initial-text
  (eval/dependencies "klangmeister.live"))

(defonce state-atom
  (reagent/atom
    {:looping? false
     :error nil
     :text initial-text
     :music (-> initial-text eval/uate :value)}))

(defn reload! []
  (swap! state-atom identity))

(defn mount-root []
  (reagent/render
    [view/render (framework/handler-for state-atom) state-atom]
    js/document.body))

(defn main []
  (mount-root))

(main)
