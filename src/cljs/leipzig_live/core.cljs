(ns leipzig-live.core
  (:require
    [leipzig-live.processing :as processing]
    [leipzig-live.views :as view]
    [leipzig-live.framework :as framework]
    [reagent.core :as reagent]))

(def initial-text "(music/phrase [1 1 2] [100 200 300])")

(defonce state-atom
  (reagent/atom
    {:looping? false
     :text initial-text
     :music (processing/evaluate initial-text)}))

(defn reload! []
  (swap! state-atom identity))

(defn mount-root []
  (reagent/render
    [view/home-page (framework/handler-for state-atom) state-atom]
    js/document.body))

(defn init! []
  (mount-root))
