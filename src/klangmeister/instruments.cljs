(ns klangmeister.instruments
  (:require [klangmeister.music :as music]))

(def context (js/window.AudioContext.))
(defn bell! [midi start dur]
  (let [freq (music/equal-temperament midi)
        start (+ start (.-currentTime context))
        stop (+ start dur)
        harmonic (fn [n proportion]
                   (let [mid (+ start 0.01)
                         gainNode (doto (.createGain context)
                                    (.connect (.-destination context)))
                         gain (doto (.-gain gainNode)
                                (.setValueAtTime 0 start)
                                (.linearRampToValueAtTime (* 0.05 proportion) mid)
                                (.linearRampToValueAtTime 0 (+ mid (* proportion 1))))]
                     (doto (.createOscillator context)
                       (.connect gainNode)
                       (-> .-frequency .-value (set! (* n freq)))
                       (-> .-type  (set! "sine"))
                       (.start start)
                       (.stop (+ start 1.5)))))]
    (harmonic 1 1)
    (harmonic 2 0.6)
    (harmonic 3 0.4)
    (harmonic 4.1 0.25)
    (harmonic 5.2 0.2)))
