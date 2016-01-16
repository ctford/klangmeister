(ns klangmeister.music
  (:require [leipzig.temperament :as temperament]
            [klangmeister.instruments :as instrument]))

(defn play-on! [notes]
  (doseq [{:keys [time instrument] :as note} notes]
    (let [synth! (or instrument instrument/bell!)
          at (+ time (.-currentTime instrument/context)) ]
      (-> note
          (update :pitch temperament/equal)
          (dissoc :time)
          synth!
          (apply [at instrument/context])))))
