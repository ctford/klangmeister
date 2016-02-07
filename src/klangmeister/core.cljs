(ns klangmeister.core
  (:require
    [klangmeister.processing] ; Import action defs.
    [klangmeister.eval :as eval]
    [klangmeister.actions :as action]
    [klangmeister.views :as view]
    [klangmeister.framework :as framework]
    [reagent.core :as reagent])
  (:require-macros
    [klangmeister.macros :as macro]))

(defn empty-state []
  (reagent/atom
    {:main {:looping? false
            :error nil
            :text ""
            :music []}}))

(defonce state-atom (empty-state))

(defn reload! []
  (swap! state-atom identity))

(defn mount-root []
  (let [handle! (framework/handler-for state-atom)]
    (handle! (action/->Refresh (macro/text "src/klangmeister/live.cljs.txt") :main))
    (reagent/render
      [view/render handle! state-atom]
      js/document.body)))

(defn main []
  (mount-root))

(main)
