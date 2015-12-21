(ns leipzig-live.views
  (:require
    [leipzig-live.actions :as action]))

(defn home-page [handle! state-atom]
  (let [state @state-atom]
    [:div [:h1 "Welcome to Leipzig Live!"]
     [:div [:input {:type "text"
                    :value (:text state)
                    :on-change #(-> % .-target .-value action/->Refresh handle!)}]
      (if-not (:looping? state)
        [:button {:on-click #(handle! (action/->Play))} "Play"]
        [:button {:on-click #(handle! (action/->Stop))} "Stop"])]
     [:div (-> state :music print-str)]]))
