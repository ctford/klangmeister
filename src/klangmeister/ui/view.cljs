(ns klangmeister.ui.view
  (:require
    [klangmeister.ui.jam :as jam]
    [klangmeister.ui.composition :as composition]
    [klangmeister.ui.reference :as reference]
    [klangmeister.ui.about :as about]
    [klangmeister.ui.synthesis-tutorial :as synthesis]))

(defn ribbon []
  [:a {:href "https://github.com/ctford/klangmeister"}
   [:img {:style {:position "absolute" :top 0 :right 0 :border 0}
          :src "https://camo.githubusercontent.com/652c5b9acfaddf3a9c326fa6bde407b87f7be0f4/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f6f72616e67655f6666373630302e706e67"
          :alt "Fork me on GitHub"
          :data-canonical-src "https://s3.amazonaws.com/github/ribbons/forkme_right_orange_ff7600.png"}]])

(defn tabs []
  [:div {:id "menu"}
   [:ul
    [:li [:a {:href "synthesis"} "Synthesis"]]
    [:li [:a {:href "composition"} "Composition"]]
    [:li [:a {:href "performance"} "Performance"]]
    [:li [:a {:href "reference"} "Reference"]]
    [:li [:a {:href "about"} "About"]]]])

(defn frame [content]
  [:div
   [:h1 [:a {:href "/klangmeister/"} "Klangmeister"]]
   [tabs]
   content
   [ribbon]])

(defn performance [handle! state-atom]
  (frame [jam/render handle! state-atom]))

(defn synthesis [handle! state-atom]
  (frame [synthesis/render handle! state-atom]))

(defn about [handle! state-atom]
  (frame [about/render handle! state-atom]))

(defn composition [handle! state-atom]
  (frame [composition/render handle! state-atom]))

(defn reference [handle! state-atom]
  (frame [reference/render handle! state-atom]))
