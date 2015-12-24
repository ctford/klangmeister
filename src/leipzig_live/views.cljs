(ns leipzig-live.views
  (:require
    [leipzig-live.actions :as action]
    [quil.core :as quil :include-macros true]
    [quil.middleware :as middleware]
    [reagent.core :as reagent]))

(quil/defsketch sketch
  :setup (constantly {:x 1})
  :update #(update % :x inc)
  :draw (fn [state] (quil/background 163) (quil/fill 0) (quil/ellipse (rem (:x state) 300) 42 51 52))
  :host "graph"
  :no-start true
  :middleware [middleware/fun-mode]
  :size [300 300])

(defn graph [handle! state]
  (reagent/create-class
    {:render (fn [] [:canvas#graph {:width 300 :height 300}])
     :component-did-mount sketch}))

(defn editor-did-mount [handle! _]
  (fn [this]
    (let [pane (.fromTextArea
                 js/CodeMirror
                 (reagent/dom-node this)
                 #js {:mode "clojure"
                      :lineNumbers true})]
      (.on pane "change" #(-> % .getValue action/->Refresh handle!)))))

(defn editor [handle! state]
  (reagent/create-class
    {:render (fn [] [:textarea {:default-value (:text state)
                                :auto-complete "off"}])
     :component-did-mount (editor-did-mount handle! state)}))

(defn home-page [handle! state-atom]
  (let [state @state-atom
        button (if-not (:looping? state)
                 [:button {:on-click #(handle! (action/->Play))} "Play"]
                 [:button {:on-click #(handle! (action/->Stop))} "Stop"])]
    [:div
     [:div [editor handle! state]]
     button
     [:div [graph handle! state]]
     [:div (str "Error? " (:error state))]
     [:div (-> state :music print-str)]]))
