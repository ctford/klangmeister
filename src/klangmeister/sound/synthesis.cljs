(ns klangmeister.sound.synthesis)

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
  "Build an envelope out of [segment-duration final-level] coordinates."
  [& corners]
  (fn [context at duration]
    (let [audio-node (.createGain context)]
      (-> audio-node .-gain (.setValueAtTime 0 at))
      (reduce
        (fn [x [dx y]]
          (-> audio-node .-gain (.linearRampToValueAtTime y (+ x dx)))
          (+ dx x))
        at
        corners)
      audio-node)))

(defn adshr [attack decay sustain hold release]
  (envelope [attack 1.0] [decay sustain] [hold sustain] [release 0]))

(defn adsr [attack decay sustain release]
  (fn [context at duration]
    (let [remainder (- duration attack decay sustain)
          hold (max 0.0 remainder)
          node (adshr attack decay sustain hold release)]
      (-> node (run-with context at duration)))))

(defn percussive [attack decay]
  (envelope [attack 1.0] [decay 0.0]))


; Combinators

(defn connect
  "Use the output of one node as the input to another."
  [node1 node2]
  (fn [context at duration]
    (let [sink (-> node2 (run-with context at duration))]
      (-> node1 (run-with context at duration) (.connect sink))
      sink)))

(defn connect->
  "Connect nodes in series."
  [& nodes]
  (reduce connect nodes))

(defn add
  "Add together nodes by connecting them all to the same gain."
  [& nodes]
  (fn [context at duration]
    (let [sink (-> (gain 1.0) (run-with context at duration))]
      (doseq [node nodes]
        (-> node (run-with context at duration) (.connect sink)))
      sink)))


; Noise

(defn noise
  "Make noise according to the supplied strategy for creating bits."
  [generate-bit!]
  (fn [context at duration]
    (let [sample-rate 44100
          frame-count (* sample-rate (+ duration 1.0)) ; Give a bit of extra for the release.
          buffer (.createBuffer context 1 frame-count sample-rate)
          data (.getChannelData buffer 0)]
      (doseq [i (range sample-rate)]
        (aset data i (generate-bit!)))
      (doto (.createBufferSource context)
        (-> .-buffer (set! buffer))
        (.start at)))))

(def white-noise
  (let [white #(-> (js/Math.random) (* 2.0) (- 1.0))]
    (noise white)))

(defn constant
  "Make a constant value by creating noise with a fixed value."
  [x]
  (noise (constantly x)))

; Oscillators

(defn oscillator
  [type freq]
  (fn [context at duration]
    (doto (.createOscillator context)
      (-> .-frequency .-value (set! 0))
      (-> .-frequency (plug freq context at duration))
      (-> .-type (set! type))
      (.start at)
      (.stop (+ at duration 1.0))))) ; Give a bit extra for the release

(def sine (partial oscillator "sine"))
(def sawtooth (partial oscillator "sawtooth"))
(def square (partial oscillator "square"))
(def triangle (partial oscillator "triangle"))


; Filters

(defn biquad-filter
  ([type freq q]
   (fn [context at duration]
     (doto (-> (biquad-filter type freq) (run-with context at duration))
       (-> .-Q (plug q context at duration)))))
  ([type freq]
   (fn [context at duration]
     (doto (.createBiquadFilter context)
       (-> .-frequency .-value (set! 0))
       (-> .-frequency (plug freq context at duration))
       (-> .-type (set! type))))))

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
