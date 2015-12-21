(ns leipzig-live.core
  (:require
    [leipzig-live.processing] ; Import action definitions.
    [leipzig-live.views :as view]
    [leipzig-live.framework :as framework]
    [reagent.core :as reagent]))

(defn mount-root []
  (let [state-atom (reagent/atom
                     {:looping? false
                      :music [[1 (- 1 0.5) 1.5] [100 (+ 100 25) (* 3 50)]]
                      :text "'((1 (- 1 0.5) 1.5) (100 (+ 100 25) (* 3 50)))"})]
    (reagent/render
      [view/home-page (framework/handler-for state-atom) state-atom]
      js/document.body)))

(defn init! []
  (mount-root))
