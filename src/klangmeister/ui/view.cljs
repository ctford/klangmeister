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

(defn link [current this href title]
  (if (= current this)
    title
    [:a {:href href} title]))

(defn tabs [current]
  [:div {:id "menu"}
   [:ul
    [:li (link current :synthesis "/klangmeister/synthesis" "Synthesis")]
    [:li (link current :composition "/klangmeister/composition" "Composition")]
    [:li (link current :performance "/klangmeister/performance" "Performance")]
    [:li (link current :reference "/klangmeister/reference" "Reference")]
    [:li (link current :about "/klangmeister/about" "About")]]])

(defn frame [current content]
  [:div
   [:h1 [:a {:href "/klangmeister/"} "Klangmeister"]]
   [tabs current]
   content
   [ribbon]])

(defn performance [handle! state-atom]
  (frame :performance [jam/render handle! state-atom]))

(defn synthesis [handle! state-atom]
  (frame :synthesis [synthesis/render handle! @state-atom]))

(defn about [handle! state-atom]
  (frame :about [about/render handle! @state-atom]))

(defn composition [handle! state-atom]
  (frame :composition [composition/render handle! @state-atom]))

(defn reference [handle! state-atom]
  (frame :reference [reference/render handle! @state-atom]))
