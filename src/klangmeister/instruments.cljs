(ns klangmeister.instruments
  (:require [klangmeister.music :as music]))

(defonce context (js/window.AudioContext.))

(defn perc [at attack decay]
  (let [node (.createGain context)]
    (doto (.-gain node)
      (.setValueAtTime 0 at)
      (.linearRampToValueAtTime 1 (+ at attack))
      (.linearRampToValueAtTime 0 (+ at attack decay)))
    node))

(defn connect [& [a b & nodes]]
  (when b
    (.connect a b)
    (apply connect (cons b nodes))))

(defn vol [x]
  (doto (.createGain context)
    (-> .-gain .-value (set! x))))

(defn bell! [midi start dur]
  (let [freq (music/equal-temperament midi)
        start (+ start (.-currentTime context))
        harmonic (fn [n proportion]
                   (doto (.createOscillator context)
                     (-> .-frequency .-value (set! (* n freq)))
                     (-> .-type (set! "sine"))
                     (.start start)
                     (.stop (+ start 1.5))
                     (connect (vol (* 0.05 proportion))
                              (perc start 0.01 proportion)
                              (.-destination context))))]
    (doseq [h [1.0 2.0 3.0 4.1 5.2]
            p [1.0 0.6 0.4 0.3 0.2]]
      (harmonic h p))))

(defn fuzz! [midi start dur]
  (let [freq (music/equal-temperament midi)
        start (+ start (.-currentTime context))
        envelope (perc start 0.1 0.5)]
    (doto (.createOscillator context)
      (-> .-frequency .-value (set! freq))
      (-> .-type  (set! "sawtooth"))
      (.start start)
      (.stop (+ start 1.5))
      (connect (vol 0.8) envelope (.-destination context)))))
