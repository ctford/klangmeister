(ns klangmeister.instruments)

(defonce context (js/window.AudioContext.))

(defn gain [at peak]
  (let [node (.createGain context)]
    (doto (.-gain node)
      (.setValueAtTime peak at))
    node))

(defn perc [at attack decay]
  (let [node (.createGain context)]
    (doto (.-gain node)
      (.setValueAtTime 0 at)
      (.linearRampToValueAtTime 1.0 (+ at attack))
      (.linearRampToValueAtTime 0 (+ at attack decay)))
    node))

(defn connect [& [a b & nodes]]
  (when b
    (.connect a b)
    (apply connect (cons b nodes))))

(defn oscillator [type freq]
  (doto (.createOscillator context)
    (-> .-frequency .-value (set! freq))
    (-> .-type (set! type))))

(def sin-osc (partial oscillator "sine"))
(def saw (partial oscillator "sawtooth"))

(defn from [osc start stop]
  (doto osc
    (.start start)
    (.stop stop)))

(defn bell! [{:keys [time duration pitch]}]
  (let [harmonic (fn [n proportion]
                   (doto (sin-osc (* n pitch))
                     (from time (+ time 1.5))
                     (connect (perc time 0.01 proportion)
                              (gain time 0.01)
                              (.-destination context))))]
    (doseq [h [1.0 2.0 3.0 4.1 5.2]
            p [1.0 0.6 0.4 0.3 0.2]]
      (harmonic h p))))

(defn fuzz! [{:keys [time duration pitch]}]
  (let [envelope (perc time 0.1 0.5)]
    (doto (saw pitch)
      (from time (+ time 1.5))
      (connect envelope (gain time 0.3) (.-destination context)))))

(defn buzz! [{:keys [time duration pitch]}]
  (let [freqs [pitch (* pitch 1.01) (* pitch 0.99)]
        envelopes [(perc time 0.3 0.2)
                   (perc time 0.05 0.1)
                   (perc time 0.1 0.1)]]
    (doseq [freq freqs envelope envelopes]
      (doto (saw freq)
        (from time (+ time 1.5))
        (connect envelope (gain time 0.05) (.-destination context))))))
