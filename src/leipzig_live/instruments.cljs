(ns leipzig-live.instruments)

(def context (js/window.AudioContext.))
(defn beep! [freq start dur]
  (let [start (+ start (.-currentTime context))
        stop (+ start dur)]
    (doto (.createOscillator context)
      (.connect (.-destination context))
      (-> .-frequency .-value (set! freq))
      (-> .-type (set! "square"))
      (.start start)
      (.stop stop))))
