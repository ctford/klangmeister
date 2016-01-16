(ns klangmeister.music
  (:require [leipzig.temperament :as temperament]
            [klangmeister.instruments :as instrument]))

(defonce context (js/window.AudioContext.))

(defn play-on! [notes]
  (doseq [{:keys [time instrument] :as note} notes]
    (let [synth (or instrument instrument/bell!)
          at (+ time (.-currentTime context))
          synth-instance (-> note
                             (update :pitch temperament/equal)
                             (dissoc :time)
                             synth)
          connected-instance (instrument/connect synth-instance instrument/destination)]
      (connected-instance at context))))
