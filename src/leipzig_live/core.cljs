(ns leipzig-live.core
  (:require
    [leipzig-live.processing] ; Import action defs.
    [leipzig-live.eval :as eval]
    [leipzig-live.views :as view]
    [leipzig-live.framework :as framework]
    [reagent.core :as reagent]))

(def initial-text
  "(->> (phrase [1 1 1 1] [0 1 2 0])
       (where :time (bpm 90))
       (where :duration (bpm 90))
       (where :pitch (comp equal-temperament B major)))")

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
