(ns leipzig-live.core
  (:require
    [leipzig-live.processing :as processing]
    [leipzig-live.views :as view]
    [leipzig-live.framework :as framework]
    [reagent.core :as reagent]))

(def initial-text
  "(->> (music/phrase [1 1 1 1] [0 1 2 0])
       (music/where :time (music/bpm 90))
       (music/where :duration (music/bpm 90))
       (music/where :pitch (comp music/equal-temperament music/B music/major)))")

(defonce state-atom
  (reagent/atom
    {:looping? false
     :error nil
     :text initial-text
     :music (-> initial-text processing/evaluate :value)}))

(defn reload! []
  (swap! state-atom identity))

(defn mount-root []
  (reagent/render
    [view/home-page (framework/handler-for state-atom) state-atom]
    js/document.body))

(defn main []
  (mount-root))

(main)
