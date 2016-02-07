(ns klangmeister.synthesis)

; Plumbing

(defn run-with
  "Convert a node (actually a reader fn) into a concrete audio node by supplying context and timing."
  [node context at duration]
  (node context at duration))

(defn destination [context at duration]
  (.-destination context))

(defn plug [param input context at duration]
  "Plug an input into an audio parameter, accepting both numbers and nodes."
  (if (number? input)
    (.setValueAtTime param input at)
    (-> input (run-with context at duration) (.connect param))))

(defn gain [level]
  (fn [context at duration]
    (doto (.createGain context)
      (-> .-gain (plug level context at duration)))))


; Envelopes

(defn envelope
  "Build an envelope out of [dx y] coordinates, starting at [0 0]."
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
  (envelope [attack 1.0] [decay sustain] [hold sustain] [release 0]))

(defn adsr [attack decay sustain release]
  (fn [context at duration]
    (let [remainder (- duration attack decay sustain)
          hold (max 0.0 remainder)
          ugen (adshr attack decay sustain hold release)]
      (-> ugen (run-with context at duration)))))

(defn percussive [attack decay]
  (envelope [attack 1.0] [decay 0.0]))


; Combinators

(defn connect
  [ugen1 ugen2]
  (fn [context at duration]
    (let [sink (-> ugen2 (run-with context at duration))]
      (-> ugen1 (run-with context at duration) (.connect sink))
      sink)))

(defn connect-> [& nodes]
  (reduce connect nodes))

(defn add [& ugens]
  (fn [context at duration]
    (let [sink (-> (gain 1.0) (run-with context at duration))]
      (doseq [ugen ugens]
        (-> ugen (run-with context at duration) (.connect sink)))
      sink)))


; Noise

(defn noise [generate-bit! duration]
  (fn [context at duration]
    (let [sample-rate 44100
          frame-count (* sample-rate duration)
          buffer (.createBuffer context 1 frame-count sample-rate)
          data (.getChannelData buffer 0)]
      (doseq [i (range sample-rate)]
        (aset data i (generate-bit!)))
      (doto (.createBufferSource context)
        (-> .-buffer (set! buffer))
        (.start at)))))

(def white-noise
  (let [white #(-> (js/Math.random) (* 2.0) (- 1.0))]
    (partial noise white)))


; Oscillators

(defn oscillator
  ([type freq detune]
   (fn [context at duration]
     (doto (-> (oscillator type freq) (run-with context at duration))
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


; Filters

(defn biquad-filter [type freq]
  (fn [context at duration]
    (doto (.createBiquadFilter context)
      (-> .-frequency (plug freq context at duration))
      (-> .-type (set! type)))))

(def low-pass (partial biquad-filter "lowpass"))
(def high-pass (partial biquad-filter "highpass"))


; Effects

(defn stereo-panner [pan]
  (fn [context at duration]
    (doto (.createStereoPanner context)
      (-> .-pan (plug pan context at duration)))))

(defn delay-line
  [time]
  (fn [context at duration]
    (let [maximum 5]
      (doto (.createDelay context maximum)
        (-> .-delayTime (plug time context at duration))))))
