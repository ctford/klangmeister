(ns klangmeister.instruments)

(defn gain [level]
  (fn [at context]
    (doto (.createGain context)
      (-> .-gain (.setValueAtTime level at)))))

(defn adshr [attack decay sustain hold release]
  (fn [at context]
    (let [node (.createGain context)]
      (doto (.-gain node)
        (.setValueAtTime 0 at)
        (.linearRampToValueAtTime 1.0 (+ at attack))
        (.linearRampToValueAtTime sustain (+ at attack decay))
        (.setValueAtTime sustain (+ at attack decay hold))
        (.linearRampToValueAtTime 0 (+ at attack decay hold release)) )
      node)))

(defn percuss [attack decay]
  (adshr attack decay 0 0 0))

(defn ashr [attack hold release]
  (adshr attack 0 1 hold release))

(defn connect
  [ugen1 ugen2]
  (fn [at context]
    (let [upstream (ugen1 at context)
          sink (ugen2 at context)]
      (.connect upstream sink)
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
