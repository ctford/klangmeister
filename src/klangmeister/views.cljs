(ns klangmeister.views
  (:require
    [klangmeister.actions :as action]
    [klangmeister.music :as music]
    [quil.core :as quil :include-macros true]
    [quil.middleware :as middleware]
    [reagent.core :as reagent]))

(defn scale [k to ms]
  (let [maximum (->> ms (map k) (apply max))
        minimum (->> ms (map k) (apply min))
        range (- maximum minimum)]
    (->> ms
         (map #(update % k - minimum))
         (map #(update % k / range))
         (map #(update % k * to)))))

(defn draw-graph [state-atom]
  (quil/sketch :draw (fn [_]
                       (quil/background 255)
                       (let [{:keys [music sync looping?]} @state-atom
                             relative-time (-> (Date.now) (- sync) (mod (music/duration music)) (/ 1000))
                             marked (map (fn [{:keys [time] :as note}]
                                           (let [played? (and looping? (<= time relative-time))]
                                             (assoc note :played? played?))) music)
                             scaled (->> marked
                                         (scale :time 550)
                                         (scale :pitch 260))]
                         (doseq [{:keys [time pitch played?]} scaled]
                           (let [colour (if played? 200 20)]
                             (quil/stroke colour)
                             (quil/fill colour)
                             (quil/ellipse
                               (-> time (+ 25))
                               (-> pitch (+ 20) - (+ 300))
                               40
                               30)))))
               :host "graph"
               :no-start true
               :middleware [middleware/fun-mode]
               :size [600 300]))

(defn graph [handle! state-atom]
  (reagent/create-class
    {:render (fn [] [:canvas#graph {:width 300 :height 300}])
     :component-did-mount #(draw-graph state-atom)}))

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
     [:div {:class "graph"} [graph handle! state-atom]]
     [:div {:class "controls"} button]
     [:div {:class (if error "error" "")} [editor handle! state]]
     [:a {:href "https://github.com/ctford/klangmeister"}
      [:img {:style {:position "absolute" :top 0 :right 0 :border 0}
             :src "https://camo.githubusercontent.com/652c5b9acfaddf3a9c326fa6bde407b87f7be0f4/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f6f72616e67655f6666373630302e706e67"
             :alt "Fork me on GitHub"
             :data-canonical-src "https://s3.amazonaws.com/github/ribbons/forkme_right_orange_ff7600.png"}]]]))
