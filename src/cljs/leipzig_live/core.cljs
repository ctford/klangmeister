(ns leipzig-live.core
  (:require
    [leipzig-live.processing] ; Import action definitions.
    [leipzig-live.views :as view]
    [leipzig-live.framework :as framework]
    [reagent.core :as reagent :refer [atom]]))

(defn mount-root []
  (let [state-atom (atom {:music [100 120]
                          :text "'(100 120)"})]
    (reagent/render
      [view/home-page (partial framework/apply-action! state-atom) @state-atom]
      js/document.body)))

(defn init! []
  (mount-root))
