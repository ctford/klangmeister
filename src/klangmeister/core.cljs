(ns klangmeister.core
  (:require
    [klangmeister.processing] ; Import action defs.
    [klangmeister.eval :as eval]
    [klangmeister.views :as view]
    [klangmeister.framework :as framework]
    [reagent.core :as reagent]))

(def initial-text
  "(->> (phrase [1 1/2 1/2 1 1 2 2]
             [0 1 0 2 -3 -10 -10])
     (with (phrase [1 1/2 1/2 1 1 1/2 1/2 1/2 1/2 2]
                   [4 4 5 4 7 6 7 6 5 4]))
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
