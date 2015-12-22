(ns leipzig-live.core
  (:require
    [leipzig-live.processing] ; Import action definitions.
    [leipzig-live.views :as view]
    [leipzig-live.framework :as framework]
    [reagent.core :as reagent]))

(def initial-state
  {:looping? false
   :music [[1 0.5 1.5] [100 125 150]]
   :text "[[1 (- 1 0.5) 1.5] [100 (+ 100 25) (* 3 50)]]"})

(defonce state-atom (reagent/atom initial-state))

(defn reload! []
  (swap! state-atom identity))

(defn mount-root []
  (reagent/render
    [view/home-page (framework/handler-for state-atom) state-atom]
    js/document.body))

(defn init! []
  (mount-root))
