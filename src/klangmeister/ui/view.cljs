(ns klangmeister.ui.view
  (:require
    [klangmeister.ui.jam :as jam]
    [klangmeister.ui.content :as content]))

(defn ribbon []
  [:a {:href "https://github.com/ctford/klangmeister"}
   [:img {:style {:position "absolute" :top 0 :right 0 :border 0}
          :src "https://camo.githubusercontent.com/652c5b9acfaddf3a9c326fa6bde407b87f7be0f4/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f6f72616e67655f6666373630302e706e67"
          :alt "Fork me on GitHub"
          :data-canonical-src "https://s3.amazonaws.com/github/ribbons/forkme_right_orange_ff7600.png"}]])

(defn tabs []
  [:div {:id "menu"}
   [:ul
    [:li [:a {:href "/synthesis"} "Synthesis"]]
    [:li [:a {:href "/music"} "Music"]]
    [:li [:a {:href "/jam"} "Jamming"]]
    [:li [:a {:href "/about"} "About"]]]])

(defn frame [content]
  [:div
   [:h1 [:a {:href "/"} "Klangmeister"]]
   [tabs]
   content
   [ribbon]])

(defn render [handle! state-atom]
  (frame [content/render handle! state-atom]))

(defn jam [handle! state-atom]
  (frame [jam/render handle! state-atom]))

(defn content [handle! state-atom]
  (frame [content/render handle! state-atom]))

(defn about []
  (frame
    [:div
     [:p "Klangmeister is a live coding environment for the browser. It lets you design synthesisers and compose music using
         computer code - without having to install anything on your own computer."]
     [:p "I recommend starting with the " [:a {:href "/synthesis"} "synthesis tutorial"] "."]]))

(defn music []
  (frame
    [:div
     [:p "Coming soon - Klangmeister lets you compose melodies."]]))
