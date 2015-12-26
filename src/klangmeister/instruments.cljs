(ns klangmeister.instruments)

(def context (js/window.AudioContext.))
(defn beep! [freq start dur]
  (let [start (+ start (.-currentTime context))
        stop (+ start dur)
        halfway (-> stop (- start) (/ 2))
        harmonic (fn [freq n]
                   (let [gainNode (doto (.createGain context)
                                    (.connect (.-destination context)))
                         gain (doto (.-gain gainNode)
                                (.setValueAtTime 1 start)
                                (.linearRampToValueAtTime (/ 1 n) (+ start 0.05))
                                (.linearRampToValueAtTime 0 (+ start 0.4)))]
                     (doto (.createOscillator context)
                       (.connect gainNode)
                       (-> .-frequency .-value (set! (* n freq)))
                       (-> .-type  (set! "sine"))
                       (.start start)
                       (.stop stop)
                       )))]
    (harmonic freq 1)
    (harmonic freq 2)
    (harmonic freq 3)
    (harmonic freq 5)))
