(ns klangmeister.ui.jam
  (:require [klangmeister.ui.graph :as graph]
            [klangmeister.actions :as action]
            [klangmeister.ui.editor :as editor]))

(defn controls [handle! state]
  (let [{:keys [looping?]} (:main state)
        play (if-not looping?
               [:button {:on-click #(handle! (action/->Play :main))} "Loop"]
               [:button {:on-click #(handle! (action/->Stop :main))} "Stop"])]
    [:div {:class "controls"} play]))

(defn render [handle! state-atom]
  [:div
   [graph/render handle! state-atom]
   [editor/render :main (-> @state-atom :main :text) handle! @state-atom]
   [controls handle! @state-atom]
   [:div
    [:p "Now we know how to make both instruments and melodies, we can create whole songs. To make the experience more interactive, lets graph the notes we're playing, and loop them over and over again as we edit the synthesiser and the composition."] 
    [:p "If you find anything confusing, refer back to the " [:a {:href "/klangmeister/synthesis"} "synthesis tutorial"] " or the " [:a {:href "/klangmeister/composition"} " composition tutorial"] ". There is a list of available functions in the " [:a {:href "/klangmeister/reference"} "reference"] "."]]])
