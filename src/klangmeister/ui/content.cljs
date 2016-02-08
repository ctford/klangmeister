(ns klangmeister.ui.content
  (:require [klangmeister.ui.editor :as editor]
            [klangmeister.actions :as action]))

(defn controls [k handle! state]
  (let [play [:button {:on-click #(handle! (action/->PlayOnce k))} "Test"]]
    [:div {:class "controls"} play]))

(def steps
  {:sine
   ["Sound is made up of pressure waves in the air. Here is the simplest possible synthesiser - a bare sine wave
    that plays the pitch of a note.
    We test it on a phrase consisting of a single middle C (which has midi code 72)."
"(defn bare-sine [note]
  (sine (:pitch note))) ; Everything after a ';' is ignored.
                        ; This lets you write little comments.

(->> (phrase [1] [72]) ; What happens if you put 62 instead of 72?
     (all :instrument bare-sine))"]
    :gain
   ["That's a bit loud. To control the volume of a sound, we alter the amplitude of the wave. To do that, we use something called a gain. A gain multiplies the amplitude by a specified factor. We use it by connecting our sine wave to it."
"(defn quiet-sine [note]
  (connect-> (sine (:pitch note))
             (gain 0.1))) ; What happens if you increase the gain?

(->> (phrase [1] [72])
     (all :instrument quiet-sine))"]
   :interference
   ["To prove sound is made from sine waves, let's add together two waves that have slightly different frequencies.
    As they fall in and out of phase, they alternately reinforce and cancel each other out."
"(defn interference [note]
  (connect-> (add (sine (:pitch note))
                  (sine (* 1.01 (:pitch note)))) ; What happens if the two frequencies are closer?
             (gain 0.1)))

(->> (phrase [1] [72])
     (all :instrument interference))"]
   :envelope
   ["The sound cuts off too abruptly. In order to shape it into a real note, we need what's called an envelope. The simplest example of an envelope is a percussive envelope. A percussive envelope is defined by an attack - how long it takes the note to get to maximum loundness, and a decay - how long it takes the note to die away. We use the envelope by connecting the sine wave to it."
"(defn plink [note]
  (connect-> (add (sine (:pitch note))
                  (sine (* 1.01 (:pitch note))))
             (percussive 0.001 0.4) ; What happens if you reverse the two numbers?
             (gain 0.1)))

(->> (phrase [1] [72])
  (all :instrument plink))"]
   :oscillators
   ["Sine waves are the simplest oscillators, but there are periodic waves that also produce sound.  "
"(defn plonk [note]
  (connect-> (add (square (:pitch note)) ; What happens if you use a sawtooth wave?
                  (triangle (* 1.01 (:pitch note))))
             (percussive 0.001 0.4)
             (gain 0.1)))

(->> (phrase [1] [72])
  (all :instrument plonk))"]
   :filters
   ["Complicated waves like triangle waves can also be thought of as the sum of a series of sine waves that get higher and higher.
    Because of that, complicated waves can be shaped by adding filters. This one blocks all frequencies above 600 hertz."
"(defn plonk [note]
  (connect-> (add (square (:pitch note))
                  (triangle (* 1.01 (:pitch note))))
             (low-pass 600) ; What happens if you raise or lower the cutoff?
             (percussive 0.001 0.4)
             (gain 0.1)))

(->> (phrase [1] [72])
  (all :instrument plonk))"]
   :sustain
   ["Many instruments can sustain a note over a period of time. To achive that, we use an adsr envelope instead of a percussive one. As well as an initial attack and decay, an adsr envelope holds the sound at a sustain level before dying away over the release."
"(defn plooonk [note]
  (connect-> (add (square (:pitch note))
                  (triangle (* 1.01 (:pitch note))))
             (low-pass 600)
             (adsr 0.001 0.4 0.5 0.1)
             (gain 0.1)))

(->> (phrase [8] [72]) ; This time the note is held eight times as long.
  (all :instrument plooonk))"]})

(defn render-one [k handle! state-atom]
  (let [[text code] (steps k)]
    [:div
     [:p text]
     [editor/render k code handle! @state-atom]
     [controls k handle! @state-atom]]))

(defn render [handle! state-atom]
  [:div
   [render-one :sine handle! state-atom]
   [render-one :gain handle! state-atom]
   [render-one :interference handle! state-atom]
   [render-one :envelope handle! state-atom]
   [render-one :oscillators handle! state-atom]
   [render-one :filters handle! state-atom]
   [render-one :sustain handle! state-atom]])
