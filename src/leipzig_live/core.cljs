(ns leipzig-live.core
  (:require
    [leipzig-live.processing] ; Import action defs.
    [leipzig-live.eval :as eval]
    [leipzig-live.views :as view]
    [leipzig-live.framework :as framework]
    [reagent.core :as reagent]))

(def initial-text
  "(->> (phrase [1 1/2 1/2 1 1 2 2] [0 1 0 2 -3 -10 -10])
     (where :time (bpm 180))
     (where :duration (bpm 180))
     (where :pitch (comp equal-temperament C sharp major)))")

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
    [view/home-page (framework/handler-for state-atom) state-atom]
    js/document.body))

(defn main []
  (mount-root))

(main)
