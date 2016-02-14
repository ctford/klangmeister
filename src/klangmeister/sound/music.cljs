(ns klangmeister.sound.music
  (:require [leipzig.temperament :as temperament]
            [klangmeister.sound.synthesis :as synthesis]))

(defonce context (or (js/window.AudioContext.) (js/window.webkitAudioContext.)))

(defn play! [notes]
  (doseq [{:keys [time duration instrument] :as note} notes]
    (let [at (+ time (.-currentTime context))
          synth-instance (-> note
                             (update :pitch temperament/equal)
                             (dissoc :time)
                             instrument)
          connected-instance (synthesis/connect synth-instance synthesis/destination)]
      (connected-instance context at duration))))
