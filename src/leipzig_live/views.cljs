(ns leipzig-live.views
  (:require
    [leipzig-live.actions :as action]
    [quil.core :as quil :include-macros true]
    [quil.middleware :as middleware]
    [reagent.core :as reagent]))

(defn draw-graph [{:keys [music]}]
  (quil/sketch :setup (constantly music)
               :draw (fn [state]
                       (quil/background 255)
                       (doseq [{:keys [time pitch]} state]
                         (quil/ellipse
                           (-> time (* 100) (+ 17))
                           (-> pitch - (* 0.25) (+ 300))
                           30
                           30)))
               :host "graph"
               :no-start true
               :middleware [middleware/fun-mode]
               :size [600 300]))

(defn graph [handle! state]
  (reagent/create-class
    {:render (fn [] [:canvas#graph {:width 300 :height 300}])
     :component-did-update #(draw-graph state)}))

(defn editor-did-mount [handle! _]
  (fn [this]
    (let [pane (.fromTextArea
                 js/CodeMirror
                 (reagent/dom-node this)
                 #js {:mode "clojure"})]
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
                 [:button {:on-click #(handle! (action/->Stop))} "Stop"])
        error (:error state)]
    [:div
     [:div {:class "graph"} [graph handle! state]]
     [:div {:class "controls"} button]
     [:div {:class (if error "error" "")} [editor handle! state]]]))
