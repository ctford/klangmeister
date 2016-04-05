(ns klangmeister.sound.instruments
  (:require
    [cljs-bach.synthesis :refer
     [connect-> percussive adsr adshr sine square sawtooth add gain high-pass low-pass white-noise
      triangle constant envelope]]))

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

(defn high-hat
  "An imitation high-hat, made with white noise."
  [decay]
  (fn [_]
    (connect->
      white-noise
      (percussive 0.01 decay)
      (high-pass 3000)
      (low-pass 4500)
      (gain 0.1))))

(def open-hat (high-hat 0.4))
(def closed-hat (high-hat 0.05))

(defn tom [pitch decay]
  (fn [_]
    (connect->
      (add
        (sawtooth pitch)
        (sawtooth (connect-> (constant pitch) (envelope [0 1] [0.5 0.5]))))
      (low-pass (* 4 pitch))
      (percussive 0.005 decay)
      (gain 0.3))))

(def kick (tom 55 0.1))

(defn organ [note]
  (connect->
    (add (sine (* 0.5 (:pitch note))) (triangle (:pitch note)))
    (low-pass (* 4 (:pitch note)) (connect-> (sine 3) (gain 3)))
    (adsr 0.1 0 1 0.3)
    (gain 0.2)))

(defn marimba [{:keys [pitch]}]
  (connect->
    (add (sine pitch) (sine (inc pitch)) (sine (* 2 pitch)))
    (adshr 0.01 0.2 0.2 0.2 0.3)
    (gain 0.1)))

(defn wah [{:keys [pitch]}]
  (connect->
    (sawtooth pitch)
    (low-pass
      (connect->
        (constant (* 4 pitch))
        (adsr 0.1 0.2 0.4 0.3)) 5)
    (adsr 0.3 0.5 0.8 0.3)
    (gain 0.3)))
