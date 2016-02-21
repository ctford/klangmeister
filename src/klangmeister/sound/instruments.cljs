(ns klangmeister.sound.instruments
  (:require
    [klangmeister.sound.synthesis :refer
     [connect-> percussive adsr sine square add gain high-pass low-pass white-noise
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

(defn high-hat [decay]
  (fn [_]
    (connect->
      white-noise
      (percussive 0.01 0.4)
      (high-pass 3000)
      (low-pass 4500)
      (gain 0.2))))

(def open-hat 0.4)
(def closed-hat 0.1)

(defn tom [pitch decay]
  (fn [_]
    (connect->
      (add
        (sine pitch)
        (triangle (connect-> (constant pitch) (envelope [0 1] [0.5 0.5]))))
      (low-pass (* 3 pitch))
      (percussive 0.01 decay)
      (gain 0.2))))

(def kick (tom 50 0.2))

(defn organ [note]
  (connect->
    (add (sine (* 0.5 (:pitch note))) (triangle (:pitch note)))
    (low-pass (* 4 (:pitch note)) (connect-> (sine 3) (gain 3)))
    (adsr 0.1 0 1 0.3)
    (gain 0.1)))
