(ns klangmeister.sound.music
  (:require [leipzig.temperament :as temperament]
            [cljs-bach.synthesis :as synthesis]))

(defn play!
  "Take a sequence of notes and play them in an audiocontext."
  [audiocontext notes]
  (doseq [{:keys [time duration instrument] :as note} notes]
    (let [at (+ time (.-currentTime audiocontext))
          synth-instance (-> note
                             (update :pitch temperament/equal)
                             (dissoc :time)
                             instrument)
          connected-instance (synthesis/connect synth-instance synthesis/destination)]
      (connected-instance audiocontext at duration))))
