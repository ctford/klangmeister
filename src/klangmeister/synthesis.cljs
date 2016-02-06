(ns klangmeister.synthesis)

(defn plug [param input context at duration]
  "Plug an input into an audio parameter,
  accepting both numbers and ugens."
  (if (fn? input)
    (.connect (input context at duration) param)
    (.setValueAtTime param input at)))

(defn gain [level]
  (fn [context at duration]
    (doto (.createGain context)
      (-> .-gain (plug level context at duration)))))

(defn line
  "Build a line out of [dx y] coordinates, starting at [0 0]."
  [& corners]
  (fn [context at duration]
    (let [node (.createGain context)
          gain (.-gain node)]
      (.setValueAtTime gain 0.0 at)
      (reduce
        (fn [x [dx y]]
          (.linearRampToValueAtTime gain y (+ x dx))
          (+ dx x))
        at
        corners)
      node)))

(defn adshr [attack decay sustain hold release]
  (line [attack 1.0] [decay sustain] [hold sustain] [release 0]))

(defn adsr [attack decay sustain release]
  (fn [context at duration]
    (let [remainder (- duration attack decay sustain)
          hold (max 0.0 remainder)
          ugen (adshr attack decay sustain hold release)]
      (ugen context at duration))))

(defn percussive [attack decay]
  (line [attack 1.0] [decay 0.0]))

(defn connect
  [ugen1 ugen2]
  (fn [context at duration]
    (let [sink (ugen2 context at duration)]
      (.connect (ugen1 context at duration) sink)
      sink)))

(defn connect-> [& nodes]
  (reduce connect nodes))

(defn noise [bit duration]
  (fn [context at duration]
    (let [sample-rate 44100
          frame-count (* sample-rate duration)
          buffer (.createBuffer context 1 frame-count sample-rate)
          data (.getChannelData buffer 0)]
      (doseq [i (range sample-rate)]
        (aset data i (bit)))
      (doto (.createBufferSource context)
        (-> .-buffer (set! buffer))
        (.start at)))))

(def white-noise (partial noise #(-> (js/Math.random) (* 2.0) dec)))

(defn oscillator
  ([type freq detune]
   (fn [context at duration]
     (doto ((oscillator type freq) context at duration)
       (-> .-frequency (plug detune context at duration)))))
  ([type freq]
   (fn [context at duration]
     (doto (.createOscillator context)
       (-> .-frequency .-value (set! freq))
       (-> .-type (set! type))
       (.start at)
       (.stop (+ at duration 1.0))))))

(def sine (partial oscillator "sine"))
(def sawtooth (partial oscillator "sawtooth"))
(def square (partial oscillator "square"))
(def triangle (partial oscillator "triangle"))

(defn biquad-filter [type freq]
  (fn [context at duration]
    (doto (.createBiquadFilter context)
      (-> .-frequency (plug freq context at duration))
      (-> .-type (set! type)))))

(def low-pass (partial biquad-filter "lowpass"))
(def high-pass (partial biquad-filter "highpass"))

(defn stereo-panner [pan]
  (fn [context at duration]
    (doto (.createStereoPanner context)
      (-> .-pan (plug pan context at duration)))))

(defn destination [context at duration]
  (.-destination context))

(defn add [& ugens]
  (fn [context at duration]
    (let [sink ((gain 1.0) context at duration)]
      (doseq [ugen ugens]
        (.connect (ugen context at duration) sink))
      sink)))
