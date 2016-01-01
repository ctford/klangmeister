(ns klangmeister.instruments
  (:require [klangmeister.music :as music]))

(defonce context (js/window.AudioContext.))

(defn perc [at attack peak decay]
  (let [node (.createGain context)]
    (doto (.-gain node)
      (.setValueAtTime 0 at)
      (.linearRampToValueAtTime peak (+ at attack))
      (.linearRampToValueAtTime 0 (+ at attack decay)))
    node))

(defn connect [& [a b & nodes]]
  (when b
    (.connect a b)
    (apply connect (cons b nodes))))

(defn oscillator [type freq]
  (doto (.createOscillator context)
    (-> .-frequency .-value (set! freq))
    (-> .-type (set! type))))

(def sin-osc (partial oscillator "sine"))
(def saw (partial oscillator "sawtooth"))

(defn bell! [midi start dur]
  (let [freq (music/equal-temperament midi)
        start (+ start (.-currentTime context))
        harmonic (fn [n proportion]
                   (doto (sin-osc (* n freq))
                     (.start start)
                     (.stop (+ start 1.5))
                     (connect (perc start 0.01 (* 0.05 proportion) proportion)
                              (.-destination context))))]
    (doseq [h [1.0 2.0 3.0 4.1 5.2]
            p [1.0 0.6 0.4 0.3 0.2]]
      (harmonic h p))))

(defn fuzz! [midi start dur]
  (let [freq (music/equal-temperament midi)
        start (+ start (.-currentTime context))
        envelope (perc start 0.1 0.8 0.5)]
    (doto (saw freq)
      (.start start)
      (.stop (+ start 1.5))
      (connect envelope (.-destination context)))))
