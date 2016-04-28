(ns klangmeister.compile.eval
  (:require
    [cljs.js :as cljs])
  (:require-macros
    [klangmeister.compile.macros :as macro]))

(def namespace-declaration
  (macro/literally
    (ns klangmeister.live
      (:require
        [cljs-bach.synthesis :refer
         [connect-> add           ; Synth connectors

          high-pass low-pass      ; Frequency filters

          sawtooth sine square    ; Signal generators
          triangle white-noise
          constant

          adsr gain percussive    ; Signal shapers
          adshr envelope

          stereo-panner reverb    ; Effects
          delay-line enhance]]

        [klangmeister.sound.instruments :refer
         [bell                    ; An imitation bell
          organ                   ; A simple organ synth
          marimba                 ; A simple plucked sound
          wah
          high-hat open-hat
          closed-hat tom kick]]   ; Percussion

        [leipzig.scale :refer
         [A B C D E F G           ; Keys
          flat high low sharp     ; Key modifiers
          major minor ionian      ; Scales
          dorian phrygian lydian
          mixolydian aeolian
          locrian blues pentatonic
          raise lower]]           ; Degree modifiers

        [leipzig.chord :refer
         [triad seventh ninth     ; Chords
          inversion root augment]]; Chord modifiers

        [leipzig.melody :refer
         [phrase then times with  ; Melody makers
          rhythm having
          all bpm tempo where     ; Melody modifiers
          after wherever]]))))

(def dependencies
  "A bundle of dependencies."
  (macro/sources
    clojure.set
    leipzig.chord
    leipzig.melody
    leipzig.scale
    cljs-bach.synthesis
    klangmeister.sound.instruments))

(defn loader
  "A namespace loader that looks in the dependencies bundle for required namespaces."
  [{:keys [name]} callback]
  (let [str-name (.-str name)
        source (dependencies str-name)]
    (if source
      (js/console.log (str "Loading " str-name "."))
      (js/console.log (str "Unable to load " str-name ".")))
    (callback {:lang :clj :source (str source)})))

(def state
  "A compiler state, which is shared across compilations."
  (cljs/empty-state))

(set-print-err-fn! #(js/console.log))

(defn normalise [result]
  (update result :error #(some-> % .-cause .-message)))

(defn uate
  "Evaluate a string of Clojurescript, with synthesis and music namespaces available."
  [expr-str]
  (cljs/eval-str
    state
    (str namespace-declaration expr-str)
    nil
    {:eval cljs/js-eval
     :load loader}
    normalise))
