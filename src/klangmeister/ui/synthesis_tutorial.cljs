(ns klangmeister.ui.synthesis-tutorial
  (:require [klangmeister.ui.editor :as editor]
            [klangmeister.actions :as action]))

(defn controls [k handle! state]
  (let [play [:button {:on-click #(handle! (action/->Test k))} "Test"]]
    [:div {:class "controls"} play]))

(def steps
  {:sine
   ["Sound is made up of pressure waves in the air. Here is the simplest possible synthesiser - a sine wave
    with a frequency of 440 hertz. When the button is pressed, the code in the box is evaluated to produce sound.
    Anything after a \";\" is ignored, so we can include little notes alongside the code."
    "(sine 440) ; What happens if you raise the frequency?"]
    :gain
   ["That's a bit loud. To control the volume of a sound, we alter the amplitude of the wave. To do that, we need something called a gain. A gain multiplies the amplitude by a specified factor. We use it by connecting our sine wave to it."
    "(connect->
  (sine 440)
  (gain 0.1)) ; Try adjusting the gain."]
   :interference
   ["To show that sound is made from sine waves, let's add together two waves that have slightly different frequencies.
    As they fall in and out of phase, they alternately reinforce and cancel each other out."
    "(connect->
  (add
    (sine 440)
    (sine 442)) ; What happens if the two frequencies are further apart? Why?
  (gain 0.1))"]
   :envelope
   ["The sound cuts off too abruptly. In order to shape it into a real note, we need what's called an envelope. The simplest example of an envelope is a percussive envelope. A percussive envelope is defined by an attack (how long it takes the note to get to maximum loundness) and a decay (how long it takes the note to die away). We use the envelope by adding it to our chain of connected synthesis nodes."
    "(connect->
  (add
    (sine 440)
    (sine 442))
  (percussive 0.001 0.4) ; What does it sound like if you reverse attack and decay?
  (gain 0.1))"]
   :oscillators
   ["Sine waves are the simplest oscillators, but there are other periodic waves that also produce sound. Examples include
    square waves and sawtooth waves."
    "(connect->
  (square 440) ; Try a sawtooth wave.
  (percussive 0.001 0.4)
  (gain 0.1))"]
   :filters
   ["Non-sinusoidal waves like squares can be expressed as the sum of a series of sine waves.
    The mathematical details don't concern
    us at the moment, but we can take advantage of the fact by subtly shaping sounds by filtering out some frequencies.
    One kind of frequency filter is the high-pass filter, which removes frequencies below a cutoff. A low-pass filter does the opposite, and removes frequencies above a cutoff."
    "(connect->
  (add
    (square 440)
    (sawtooth 442))
  (low-pass 600) ; What changes if you raise the cutoff frequency to 2600?
  (percussive 0.001 0.4)
  (gain 0.1))"]
   :sustain
   ["Many instruments can sustain a note over a period of time. To achieve that, we use an ADSR envelope instead of a percussive one. As well as an initial attack and decay (AD), an ADSR envelope holds the sound at a sustain level (S) before dying away over the release (R)."
    "(connect->
  (add
    (square 440)
    (sawtooth 442))
  (low-pass 600)
  (adsr 0.001 0.4 0.5 0.1) ; How does a longer attack sound?
  (gain 0.1))"]})

(defn render-one [k handle! state]
  (let [[text code] (steps k)]
    [:div
     [:p text]
     [editor/render k code handle! state]
     [controls k handle! state]]))

(defn render [handle! state]
  [:div
   [render-one :sine handle! state]
   [render-one :gain handle! state]
   [render-one :interference handle! state]
   [render-one :envelope handle! state]
   [render-one :oscillators handle! state]
   [render-one :filters handle! state]
   [render-one :sustain handle! state]
   [:div
    [:p "Now that you know how to design synthesisers, try " [:a {:href "/klangmeister/composition"} "composing melodies"] "."]]])
