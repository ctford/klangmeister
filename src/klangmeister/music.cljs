(ns klangmeister.music
  (:require [leipzig.temperament :as temperament]
            [klangmeister.synthesis :as synthesis]))

(defonce context (js/window.AudioContext.))

(defn play-on! [notes]
  (doseq [{:keys [time duration instrument] :as note} notes]
    (let [at (+ time (.-currentTime context))
          synth-instance (-> note
                             (update :pitch temperament/equal)
                             (dissoc :time)
                             instrument)
          connected-instance (synthesis/connect synth-instance synthesis/destination)]
      (connected-instance context at duration))))
