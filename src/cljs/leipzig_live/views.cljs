(ns leipzig-live.views
  (:require
    [leipzig-live.actions :as action]))

(defn home-page [handle! state]
  [:div [:h1 "Welcome to Leipzig Live!"]
   [:div [:input {:type "text"
                  :value (:text state)
                  :on-change #(-> % .-target .-value action/->Refresh handle!)}]
   [:button {:on-click (fn [_] (handle! (action/->Play)))} "Play!"]]
   [:div
    (-> state :music print)]])
