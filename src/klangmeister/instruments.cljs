(ns klangmeister.instruments
  (:require
    [klangmeister.synthesis :refer [connect-> percussive sine add gain]]))

(defn bell
  "An imitation bell, made by adding together harmonics."
  [{:keys [pitch]}]
    (let [harmonic (fn [n proportion]
                     (connect->
                       (sine (* n pitch))            ; Each harmonic is a sine wave.
                       (percussive 0.01 proportion)  ; The attack and decay of each note.
                       (gain 0.05)))]                ; Multiply the volume of each harmonic by 0.5.
      (->> (map harmonic [1.0 2.0 3.0 4.1 5.2]       ; Each harmonic is a multiple of the base frequency.
                         [1.0 0.6 0.4 0.3 0.2])      ; Higher harmonics are weaker.
           (apply add))))
