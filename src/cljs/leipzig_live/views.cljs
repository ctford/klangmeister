(ns leipzig-live.views
  (:require
    [leipzig-live.actions :as action]
    [reagent.core :as reagent]))

(defn editor-did-mount [handle! state-atom]
  (fn [this]
    (let [pane (.fromTextArea
                 js/CodeMirror
                 (reagent/dom-node this)
                 #js {:mode "clojure"
                      :lineNumbers true})]
      (.on pane "change" #(-> % .getValue action/->Refresh handle!)))))

(defn editor [handle! state-atom]
  (reagent/create-class
    {:render (fn [] [:textarea {:default-value (:text @state-atom)
                                :auto-complete "off"}])
     :component-did-mount (editor-did-mount handle! state-atom)}))

(defn home-page [handle! state-atom]
  (let [state @state-atom]
    [:div
     (if-not (:looping? state)
       [:button {:on-click #(handle! (action/->Play))} "Play"]
       [:button {:on-click #(handle! (action/->Stop))} "Stop"])
     [:div [editor handle! state-atom]]
     [:div (-> state :music print-str)]]))
