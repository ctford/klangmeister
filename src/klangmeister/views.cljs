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
      (row "->>" "Make a series of updates to a melody." "(->> (phrase [1] [0]) (where :pitch inc))")
      (row ";" "Ignore the rest of the line, so that you can put comments in." "; The melody.")
      (row "A..G" "Put a quantity of semitones into a specified key." "(A 3) => 72")
      (row "bpm" "Make a function to convert from beats into seconds." "((bpm 120) 3) => 1.5")
      (row "comp" "Join two functions together." "((comp C major) 2) => 64")
      (row "connect->" "Connect a series of synth nodes together." "(connect-> (saw 440) (low-pass 1000))")
      (row "defn" "Define a named function." "(defn double [x] (* 2 x))")
      (row "major" "Put a rank into a major scale." "(major 2) => 4")
      (row "minor" "Put a rank into a minor scale." "(minor 2) => 3")
      (row "phrase" "Use a some durations and some pitches to make a melody." "(phrase [1 1 2] [0 2 4])")
      (row "then" "Put one melody after another." "(->> bass (then guitar))")
      (row "times" "Repeat a melody several times." "(->> bass (times 4))")
      (row "where" "Update an element of a melody." "(where :pitch inc notes)")
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
