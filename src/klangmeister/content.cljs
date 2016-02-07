(ns klangmeister.content
  (:require [klangmeister.editor :as editor]
            [klangmeister.actions :as action]))

(defn controls [k handle! state]
  (let [play [:button {:on-click #(handle! (action/->PlayOnce k))} "Test"]]
    [:div {:class "controls"} play]))

(def steps
  {:sine
   ["Sound is made up of pressure waves in the air.
   Here is the simplest possible synthesiser - a bare sine wave."
   "(defn basic-synthesiser [note]
     (sine (:pitch note)))

   (->> (phrase [1] [72])
        (all :instrument basic-synthesiser))"]
   :interference
   ["To prove sound is based on sine waves, let's add together two waves that are out of phase."
   "(defn interference [note]
     (add (sine (* 1.01 (:pitch note))) (sine (:pitch note))))

   (->> (phrase [1] [72])
        (all :instrument interference))"]
   :envelope
   ["The sound cuts off abruptly. In order to shape it into a real note, we need what's called an envelope. The simplest example of an envelope is a percussive envelope. A percussive envelope is defined by an attack - how long it takes the note to get to maximum loundness, and a decay - how long it takes the note to die away."
    "(defn plink [note]
     (connect-> (sine (:pitch note))
                (percussive 0.05 0.2)))

    (->> (phrase [1] [72])
        (all :instrument plink))"]
    :gain
   ["That's a bit loud. To control the volume of a sound, we alter the amplitude of the wave. To do that, we use something called a gain. A gain multiplies the amplitude by a specified factor."
    "(defn quiet-plink [note]
    (connect-> (sine (:pitch note))
    (percussive 0.01 0.2)
    (gain 0.4)))

(->> (phrase [1] [72])
        (all :instrument quiet-plink))"]
    })

(defn render [k handle! state-atom]
  (let [[text code] (steps k)]
    [:div
     [:p text]
     [editor/render k code handle! @state-atom]
     [controls k handle! @state-atom]]))
