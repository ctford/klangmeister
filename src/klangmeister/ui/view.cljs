(ns klangmeister.ui.view
  (:require
    [klangmeister.actions :as action]
    [klangmeister.ui.graph :as graph]
    [klangmeister.ui.editor :as editor]
    [klangmeister.ui.content :as content]))

(defn controls [handle! state]
  (let [{:keys [looping?]} (:main state)
        play (if-not looping?
               [:button {:on-click #(handle! (action/->Play :main))} "Loop"]
               [:button {:on-click #(handle! (action/->Stop :main))} "Stop"])]
    [:div {:class "controls"} play]))

(defn ribbon []
  [:a {:href "https://github.com/ctford/klangmeister"}
   [:img {:style {:position "absolute" :top 0 :right 0 :border 0}
          :src "https://camo.githubusercontent.com/652c5b9acfaddf3a9c326fa6bde407b87f7be0f4/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f6f72616e67655f6666373630302e706e67"
          :alt "Fork me on GitHub"
          :data-canonical-src "https://s3.amazonaws.com/github/ribbons/forkme_right_orange_ff7600.png"}]])

(defn render [handle! state-atom]
  [:div
   [:h1 "Klangmeister"]
   [content/render handle! state-atom]
   [:p "Now we know the basics of synthesis, we can play melodies or even whole songs. To make the experience more interactive, lets graph the notes we're playing, and loop them over and over again as we edit the synth and music."]
   [graph/render handle! state-atom]
   [editor/render :main (-> @state-atom :main :text) handle! @state-atom]
   [controls handle! @state-atom]
   [ribbon]])
