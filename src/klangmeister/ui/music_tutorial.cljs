(ns klangmeister.ui.music-tutorial
  (:require [klangmeister.ui.editor :as editor]
            [klangmeister.actions :as action]))

(defn controls [k handle! state]
  (let [play [:button {:on-click #(handle! (action/->PlayOnce k))} "Play"]]
    [:div {:class "controls"} play]))

(def steps
  {:phrase
   ["A melody is a sequence of notes. Each note occurs at a particular time and with a particular pitch.
    We can compose a melody out of a list of the durations of each note and a list of the pitches of each note."
    "(phrase [3/3 3/3 2/3 1/3 3/3] ; You can change both the durations and the pitches.
        [ 72  72  72  74  76])"]
   :then
   ["We can chain together two melodies to make a longer one."
    "(->>
  (phrase [3/3 3/3 2/3 1/3 3/3] ; Can you recognise the tune yet?
          [ 72  72  72  74  76])
  (then
    (phrase [2/3 1/3 2/3 1/3 3/3]
            [ 76  74  76  77  79])))"]
   :with
   ["Alternatively, we can play two melodies at the same time. If they're well-chosen, we'll get harmony."
   "(->>
  (phrase [3/3 3/3 2/3 1/3 3/3]
          [ 72  72  72  74  76])
  (with
    (phrase [2/3 1/3 2/3 1/3 3/3]
            [ 76  74  76  77  79])))"]
   :times
   ["We can repeat melodies several times."
    "(->>
  (phrase [3/3 3/3 2/3 1/3 3/3]
          [ 72  72  72  74  76])
  (with
    (phrase [2/3 1/3 2/3 1/3 3/3]
            [ 76  74  76  77  79]))
  (times 2))"]})

(defn render-one [k handle! state-atom]
  (let [[text code] (steps k)]
    [:div
     [:p text]
     [editor/render k code handle! @state-atom]
     [controls k handle! @state-atom]]))

(defn render [handle! state-atom]
  [:div
   [render-one :phrase handle! state-atom]
   [render-one :then handle! state-atom]
   [render-one :with handle! state-atom]
   [render-one :times handle! state-atom]])
