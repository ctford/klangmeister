(ns klangmeister.views
  (:require
    [klangmeister.actions :as action]
    [klangmeister.graph :as graph]
    [klangmeister.editor :as editor]
    [reagent.core :as reagent]))

(defn render [handle! state-atom]
  (let [state @state-atom
        button (if-not (:looping? state)
                 [:button {:on-click #(handle! (action/->Play))} "Play"]
                 [:button {:on-click #(handle! (action/->Stop))} "Stop"])
        error (:error state)]
    [:div
     [:div {:class "graph"} [graph/render handle! state-atom]]
     [:div {:class "controls"} button]
     [:div {:class (if error "error" "")} [editor/render handle! state]]
     [:div
      [:table {:class "cheatsheet"}
       [:tbody
        [:tr [:td "(..)"] [:td "Evaluate an expression."]]
        [:tr [:td "[..]"] [:td "Build a sequence."]]
        [:tr [:td "->>"] [:td "Thread together multiple expressions."]]
        [:tr [:td "A..G"] [:td "Translate a relative midi into the specified key."]]
        [:tr [:td "flat"] [:td "Translate a midi down one semitone."]]
        [:tr [:td "phrase"] [:td "Build a melody from a sequence of durations and a sequence of pitches."]]
        [:tr [:td "bpm"] [:td "Build a function that converts beats into seconds."]]
        [:tr [:td "major"] [:td "Convert a rank of a minor scale into a midi pitch."]]
        [:tr [:td "minor"] [:td "Convert a rank of a minor scale into a midi pitch."]]
        [:tr [:td "sharp"] [:td "Translate a midi up one semitone."]]
        [:tr [:td "where"] [:td "Transform a specified key using a specified function."]]
        [:tr [:td "with"] [:td "Combines two melodies."]]]]]
     [:a {:href "https://github.com/ctford/klangmeister"}
      [:img {:style {:position "absolute" :top 0 :right 0 :border 0}
             :src "https://camo.githubusercontent.com/652c5b9acfaddf3a9c326fa6bde407b87f7be0f4/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f6f72616e67655f6666373630302e706e67"
             :alt "Fork me on GitHub"
             :data-canonical-src "https://s3.amazonaws.com/github/ribbons/forkme_right_orange_ff7600.png"}]]]))
