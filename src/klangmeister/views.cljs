(ns klangmeister.views
  (:require
    [klangmeister.actions :as action]
    [klangmeister.graph :as graph]
    [klangmeister.editor :as editor]))

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
      (row "(..)" "Evaluate an expression" "(+ 1 1) => 2")
      (row "[..]" "Make a sequence." "[1 2 3] => [1 2 3]")
      (row "->>" "Thread together multiple expressions." "(->> guitar (with bass))")
      (row ";" "Ignore the rest of the line, so that you can put comments in." "; The melody.")
      (row "A..G" "Transform a quantity of semitones relative to root into the specified key." "(A 3) => 72")
      (row "bpm" "Make a transformation from beats to seconds." "((bpm 120) 3) => 1.5")
      (row "comp" "Join together several transformations." "((comp C major) 2) => 64")
      (row "def" "Define a name with a value. Use it before the `->>`." "(def key (comp C major))")
      (row ":duration" "The duration of each note." "")
      (row "flat" "Transform a number of semitones down one." "(flat 72) => 71")
      (row ":instrument" "Specifies the synth to play. Currently :bell, :bop, :buzz, :omg or :fuzz." "")
      (row "major" "Transform a rank of a major scale into semitones." "(major 2) => 4")
      (row "minor" "Transform a rank of a minor scale into semitones." "(minor 2) => 3")
      (row "phrase" "Transform a sequence of durations and a sequence of pitches into a melody." "(phrase [1 1 2] [0 2 4])")
      (row ":pitch" "The pitch of each note." "")
      (row "sharp" "Transform a number of semitones up one." "(sharp 70) => 71")
      (row "then" "Put one melody after another." "(->> bass (then guitar))")
      (row ":time" "The time each note plays at." "")
      (row "where" "Transform an attribute of each note using the specified transformation." "(where :pitch inc notes)")
      (row "with" "Put two melodies together." "(with bass guitar)")]]))

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
