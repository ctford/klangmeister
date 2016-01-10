(ns klangmeister.instruments)

(defonce context (js/window.AudioContext.))

(defn volume [peak]
  (fn [at context]
    (doto (.createGain context)
      (-> .-gain (.setValueAtTime peak at)))))

(defn percuss [attack decay]
  (fn [at context]
    (let [node (.createGain context)]
      (doto (.-gain node)
        (.setValueAtTime 0 at)
        (.linearRampToValueAtTime 1.0 (+ at attack))
        (.linearRampToValueAtTime 0 (+ at attack decay)))
      node)))

(defn connect [ugen1 ugen2]
  (fn [at context]
    (let [upstream (ugen1 at context)
          sink (ugen2 at context)]
      (.connect upstream sink)
      sink)))

(defn >>> [& nodes]
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

(defn destination [at context]
  (.-destination context))

(defn blend [nodes]
  (fn [at context]
    (doseq [node nodes]
      (node at context))))

(defn bell! [{:keys [time duration pitch]}]
  (let [harmonic (fn [n proportion]
                   (>>> (sin-osc (* n pitch) 1.5)
                        (percuss 0.01 proportion)
                        (volume 0.01)
                        destination))]
    (->>
      (map
        harmonic
        [1.0 2.0 3.0 4.1 5.2]
        [1.0 0.6 0.4 0.3 0.2])
      blend)))

(defn fuzz! [{:keys [duration pitch]}]
  (>>> (saw pitch 1.5)
       (percuss 0.1 0.5)
       (volume 0.1)
       destination))

(defn buzz! [{:keys [duration pitch]}]
  (let [freqs [pitch (* pitch 1.01) (* pitch 0.99)]
        envelopes [[0.3 0.2] [0.05 0.1] [0.1 0.1]]]
    (->> (map (fn [freq [attack decay]]
                (>>> (saw freq 1.5)
                     (percuss attack decay)
                     (volume 0.05)
                     destination))
              freqs
              envelopes)
         blend)))
