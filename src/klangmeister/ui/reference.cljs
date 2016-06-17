(ns klangmeister.ui.reference)

(defn table [defs]
  [:table {:class "reference"}
   [:thead
    [:tr
     [:th ""]
     [:th "Description"]
     [:th "Usage"]]]
   [:tbody
    (map
      (fn [[name description usage]]
        [:tr
         [:td {:class "name"} name]
         [:td description]
         [:td {:class "usage"} usage]])
      defs)]])

(def signals
  {"sawtooth" "A waveform with prominent harmonics."
   "sine" "A simple sine wave."
   "square" "A wave containing only odd harmonics."
   "triangle" "A wave with subtle harmonics."
   "white-noise" "Random noise."})

(def shapers
  {"gain" "Multiply a signal by the given factor."
   "high-pass" "Cut out low frequencies."
   "low-pass" "Cut out high frequencies."})

(def envelopes
  {"adsr" "An envelope with attack, decay, sustain and release."
   "percussive" "A simple envelope with just attack and decay"})

(def combinators
  {"add" "Add two or more signals together."
   "connect->" "Connect two or more signal processors together"})

(def all (merge signals shapers envelopes combinators))

(defn render [handle! state]
  [:div
   [:h2 "Synthesis"]
   [:p "These functions are used to generate signals that can then be shaped or combined with other signals."]
   (table [["sawtooth" "Produces a wave with prominent harmonics." "(sawtooth 440)"]
           ["sine" "Produces a simple sine wave." "(sine 440)"]
           ["square" "Produces a wave containing only odd harmonics." "(square 440)"]
           ["triangle" "Produces a wave with subtle harmonics." "(triangle 440)"]
           ["white-noise" "Generates random noise." "white-noise"]])
   [:p "These functions are used to shape other signals."]
   (table [["gain" "Multiplies a signal by the given factor." "(gain 0.5)"]
           ["high-pass" "Cuts out low frequencies." "(low-pass 500)"]
           ["low-pass" "Cuts out high frequencies." "(low-pass 1000)"]])
   [:p "These functions define the shape of a note."]
   (table [["adsr" "Defines an envelope with attack, decay, sustain and release." "(adsr 0.1 0.2 0.5 0.1)"]
           ["percussive" "Defines a simple envelope with just attack and decay" "(percussive 0.1 0.3)"]])
   [:p "These functions combine signals together."]
   (table [["add" "Adds two or more signals together."
"(add
  (sine 440)
  (sine 660))"]
           ["connect->" "Connects two or more signal processors together"
"(connect->
  (sawtooth 440)
  (low-pass 800))"]])
   [:h2 "Composition"]
   [:p "These functions are used to build melodies."]
   (table [["phrase" "Turns a list of durations and a list of pitches into a melody."
"(phrase
  [1 1 2/3 1/3 1]
  [0 0   0   2 3])"]
           ])
   [:p "These functions are used to combine melodies."]
   (table [["then" "Sequences two melodies together."
"(->> intro
     (then verse))"]
           ["times" "Repeats a melody."
"(->> chorus
     (times 4))"]
           ["with" "Overlays two melodies."
"(->> bass
     (with drums))"] ])
   [:p "These functions are used to put melodies into specific keys and tempos."]
   (table [
           ["A..G" "The different musical keys."
"(->> piece
     (where :pitch (comp C major)))"]
           ["all" "Sets a property of every note in a melody."
"(->> solo
     (all :instrument guitar))"]
           ["flat, sharp" "Raise or lower a scale by a semitone"
"(->> piece
     (where :pitch (comp B flat major)))"]
           ["high, low" "Raise or lower a scale by an octave."
"(->> piece
     (where :pitch (comp low C major)))"]
           ["major" "A happy-sounding scale."
"(->> piece
     (where :pitch (comp C major)))"]
           ["minor" "A sad-sounding scale."
"(->> piece
     (where :pitch (comp D minor)))"]
           ["tempo" "Puts a melody in a tempo at a particular beats-per-minute."
"(->> piece
     (tempo (bpm 120))"]])
    [:h2 "Instruments"]
    [:p "These instruments are available by default."]
   (table [["bell" "An approximate bell sound." ""]
           ["kick" "A kick drum." ""]
           ["open-hat, closed-hat" "High hat sounds." ""]
           ["marimba" "A pure, percussive sound like a marimba." ""]
           ["wah" "A synth with a wah sound made by a filter." ""]])])
