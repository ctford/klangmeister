(ns klangmeister.views
  (:require
    [klangmeister.actions :as action]
    [klangmeister.graph :as graph]
    [klangmeister.editor :as editor]
    [reagent.core :as reagent]))

(defn controls [handle! {:keys [looping?]}]
  (let [play (if-not looping?
               [:button {:on-click #(handle! (action/->Play))} "Play"]
               [:button {:on-click #(handle! (action/->Stop))} "Stop"])]
    [:div {:class "controls"} play]))

(defn cheatsheet []
  (let [row (fn [term explanation example]
              [:tr
               [:td {:class "code"} term]
               [:td explanation]
               [:td {:class "code"} example]])]
    [:table {:class "cheatsheet"}
     [:tbody
      (row "(..)" "Evaluate an expression" "(+ 1 1)")
      (row "[..]" "Build a sequence." "[1 2 3]")
      (row "->>" "Thread together multiple expressions." "(->> (range) (take 4))")
      (row "A..G" "Translate a relative midi into the specified key." "(A 3) => 72")
      (row "bpm" "Build a function that converts beats into seconds." "((bpm 120) 3) => 1.5")
      (row "def" "Define a value with a name. Used outside the `->>`." "(def pi 3.14)")
      (row "flat" "Translate a midi down one semitone." "(flat 72) => 71")
      (row "major" "Convert a rank of a minor scale into a midi pitch." "(major 2) => 4")
      (row "minor" "Convert a rank of a minor scale into a midi pitch." "(minor 2) => 3")
      (row "phrase" "Build a melody from a sequence of durations and a sequence of pitches." "(phrase [1 1 2] [0 2 4])")
      (row "sharp" "Translate a midi up one semitone." "(sharp 70) => 71")
      (row "where" "Transform a specified key using a specified function." "(where :pitch inc notes)")
      (row "with" "Combines two melodies." "(with notes bass-notes)")]]))

(defn ribbon []
  [:a {:href "https://github.com/ctford/klangmeister"}
   [:img {:style {:position "absolute" :top 0 :right 0 :border 0}
          :src "https://camo.githubusercontent.com/652c5b9acfaddf3a9c326fa6bde407b87f7be0f4/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f6f72616e67655f6666373630302e706e67"
          :alt "Fork me on GitHub"
          :data-canonical-src "https://s3.amazonaws.com/github/ribbons/forkme_right_orange_ff7600.png"}]])

(defn render [handle! state-atom]
  [:div
   [graph/render handle! state-atom]
   [controls handle! @state-atom]
   [editor/render handle! @state-atom]
   [cheatsheet]
   [ribbon]])
