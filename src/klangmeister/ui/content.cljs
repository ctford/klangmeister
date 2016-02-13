(ns klangmeister.ui.content
  (:require [klangmeister.ui.editor :as editor]
            [klangmeister.actions :as action]))

(defn controls [k handle! state]
  (let [play [:button {:on-click #(handle! (action/->Test k))} "Test"]]
    [:div {:class "controls"} play]))

(def steps
  {:sine
   ["Sound is made up of pressure waves in the air. Here is the simplest possible synthesiser - a bare sine wave
    that plays the pitch of a note."
    "(sine 440) ; What happens if you change the frequency?"]
    :gain
   ["That's a bit loud. To control the volume of a sound, we alter the amplitude of the wave. To do that, we use something called a gain. A gain multiplies the amplitude by a specified factor. We use it by connecting our sine wave to it."
    "(connect-> (sine 440)
           (gain 0.1)) ; What happens if you increase the gain?"]
   :interference
   ["To prove sound is made from sine waves, let's add together two waves that have slightly different frequencies.
    As they fall in and out of phase, they alternately reinforce and cancel each other out."
    "(connect-> (add (sine 440) (sine 442)) ; What happens if the two frequencies are closer?
           (gain 0.1))"]
   :envelope
   ["The sound cuts off too abruptly. In order to shape it into a real note, we need what's called an envelope. The simplest example of an envelope is a percussive envelope. A percussive envelope is defined by an attack - how long it takes the note to get to maximum loundness, and a decay - how long it takes the note to die away. We use the envelope by connecting the sine wave to it."
    "(connect-> (add (sine 440) (sine 442))
           (percussive 0.001 0.4) ; What happens if you reverse the two numbers?
           (gain 0.1))"]
   :oscillators
   ["Sine waves are the simplest oscillators, but there are periodic waves that also produce sound.  "
    "(connect-> (add (square 440) (triangle 442)) ; Try a sawtooth wave.
           (percussive 0.001 0.4)
           (gain 0.1))"]
   :filters
   ["Complicated waves like triangle waves can also be thought of as the sum of a series of sine waves that get higher and higher.
    Because of that, complicated waves can be shaped by adding filters. This one blocks all frequencies above 600 hertz."
    "(connect-> (add (square 440) (triangle 442))
           (low-pass 600) ; What happens if you raise or lower the cutoff?
           (percussive 0.001 0.4)
           (gain 0.1))"]
   :sustain
   ["Many instruments can sustain a note over a period of time. To achive that, we use an ADSR envelope instead of a percussive one. As well as an initial attack and decay, an ADSR envelope holds the sound at a sustain level before dying away over the release."
    "(connect-> (add (square 440) (triangle 442))
           (low-pass 600)
           (adsr 0.001 0.4 0.5 0.1) ; What happens when you change the numbers?
           (gain 0.1))"]})

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
