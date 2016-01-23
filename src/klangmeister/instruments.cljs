(ns klangmeister.instruments)

(defn gain [level]
  (fn [at context]
    (doto (.createGain context)
      (-> .-gain (.setValueAtTime level at)))))


(defn line
  "Build a line out of [x y] coordinates, starting at [0 0]."
  [& corners]
  (fn [at context]
    (let [node (.createGain context)
          gain (.-gain node)]
      (.setValueAtTime gain 0 at)
      (reduce
        (fn [x [dx y]]
          (.linearRampToValueAtTime gain y (+ x dx))
          (+ dx x))
        at
        corners)
      node)))

(defn adshr [attack decay sustain hold release]
  (line [attack 1.0] [decay sustain] [hold sustain] [release 0]))

(defn ashr [attack hold release]
  (line [attack 1.0] [hold 1.0] [release 0]))

(defn percuss [attack decay]
  (line [attack 1.0] [decay 0.0]))

(defn connect
  [ugen1 ugen2]
  (fn [at context]
    (let [sink (ugen2 at context)]
      (.connect (ugen1 at context) sink)
      sink)))

(defn >> [& nodes]
  (reduce connect nodes))

(defn oscillator [type freq duration]
  (fn [at context]
    (doto (.createOscillator context)
      (-> .-frequency .-value (set! freq))
      (-> .-type (set! type))
      (.start at)
      (.stop (+ at duration)))))

(def sin-osc (partial oscillator "sine"))
(def saw (partial oscillator "sawtooth"))
(def square (partial oscillator "square"))

(defn modulator [freq duration]
  (fn [at context]
    (let [modulatee (saw freq duration)
          carrier (>> (sin-osc 2 duration) (gain freq))]
      ((connect carrier (.-frequency modulatee)) at context)
      modulatee)))

(defn biquad-filter [type freq]
  (fn [at context]
    (doto (.createBiquadFilter context)
      (-> .-frequency .-value (set! freq))
      (-> .-type (set! type)))))

(def lpf (partial biquad-filter "lowpass"))
(def hpf (partial biquad-filter "highpass"))

(defn destination [at context]
  (.-destination context))

(defn blend [ugen1 ugen2]
  (fn [at context]
    (let [one (ugen1 at context)
          two (ugen2 at context)
          sink ((gain 1.0) at context)]
      (.connect one sink)
      (.connect two sink)
      sink)))

(defn >< [& nodes]
  (reduce blend nodes))

(defn bell! [{:keys [time duration pitch]}]
  (let [harmonic (fn [n proportion]
                   (>> (sin-osc (* n pitch) 1.5)
                       (percuss 0.01 proportion)
                       (gain 0.01)))]
    (apply ><
           (map
             harmonic
             [1.0 2.0 3.0 4.1 5.2]
             [1.0 0.6 0.4 0.3 0.2]))))

(defn bop! [{:keys [duration pitch]}]
  (>> (square pitch 1.5)
      (adshr 0.01 0.1 0.6 0.2 0.1)
      (gain 0.1)))

(defn omg! [{:keys [duration pitch]}]
  (>> (square pitch 1.5)
      (ashr 0.1 0.4 0.05)
      (gain 0.1)))

(defn buzz! [{:keys [duration pitch]}]
  (let [freqs [pitch (* pitch 1.01) (* pitch 0.99)]
        envelopes [[0.3 0.2] [0.05 0.1] [0.1 0.1]]]
    (->> (map (fn [freq [attack decay]]
                (>> (saw freq 1.5)
                    (percuss attack decay)
                    (gain 0.05)))
              freqs
              envelopes)
         (apply ><))))
