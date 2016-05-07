(ns klangmeister.ui.about
  (:require [klangmeister.ui.editor :as editor]
            [klangmeister.actions :as action]))

(defn controls [k handle! state]
  (let [play [:button {:on-click #(handle! (action/->PlayOnce k))} "Play"]]
    [:div {:class "controls"} play]))

(defn render [handle! state]
  (let [code "(->> (phrase (repeat 1/16) [0 2 4 7 9 11 14 16 18 21])
     (where :pitch (comp C major)))"]
    [:div
     [:p "Klangmeister is a live coding environment for the browser. It lets you design synthesisers and compose music using
         computer code - without having to install anything on your own computer."]
     [editor/render :about code handle! state]
     [controls :about handle! state]
     [:p "Klangmeister works best in Chrome, because the other browsers have less stable implementations of the synthesis features
          that Klangmeister relies on. They're working on it though."]
     [:p [:a {:href "https://twitter.com/ctford"} "I"] " recommend starting with the " [:a {:href "/klangmeister/synthesis"} "synthesis tutorial"] "."]]))
