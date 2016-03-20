(ns klangmeister.sound.synthesis)

; Definitions

(defn section
  ([input output] {:input input :output output})
  ([singleton] (section singleton singleton)))

(defn generator
  "A graph of synthesis nodes without an input, so another graph can't connect to it."
  [node]
  (section nil node))

(defn sink
  "A graph of synthesis nodes without an output, so it can't connect to another graph."
  [node]
  (section node nil))

; Plumbing

(defn run-with
  "Convert a synth (actually a reader fn) into a concrete section by supplying context and timing."
  [synth context at duration]
  (synth context at duration))

(defn destination [context at duration]
  (sink (.-destination context)))

(defn plug [param input context at duration]
  "Plug an input into an audio parameter, accepting both numbers and synths."
  (if (number? input)
    (.setValueAtTime param input at)
    (-> input (run-with context at duration) :output (.connect param))))

(defn gain [level]
  (fn [context at duration]
    (section
      (doto (.createGain context)
        (-> .-gain (plug level context at duration))))))


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
      (section audio-node))))

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
  "Use the output of one synth as the input to another."
  [upstream-synth downstream-synth]
  (fn [context at duration]
    (let [{downstream-input :input downstream-output :output} (-> downstream-synth (run-with context at duration))
          {upstream-input :input upstream-output :output} (-> upstream-synth (run-with context at duration))]
      (-> upstream-output (.connect downstream-input))
      (section upstream-input downstream-output))))

(defn connect->
  "Connect synths in series."
  [& nodes]
  (reduce connect nodes))

(defn add
  "Add together synths by connecting them all to the same gain."
  [& synths]
  (fn [context at duration]
    (let [{upstream-input :input upstream-output :output} (-> (gain 1.0) (run-with context at duration))
          {downstream-input :input downstream-output :output} (-> (gain 1.0) (run-with context at duration))]
      (doseq [synth synths]
        (let [{:keys [input output]} (-> synth (run-with context at duration))]
          (-> output (.connect downstream-input))
          (when input
            (-> upstream-output (.connect input)))))
      (section upstream-input downstream-output))))


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
      (generator
        (doto (.createBufferSource context)
          (-> .-buffer (set! buffer))
          (.start at))))))

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
    (generator
      (doto (.createOscillator context)
        (-> .-frequency .-value (set! 0))
        (-> .-frequency (plug freq context at duration))
        (-> .-type (set! type))
        (.start at)
        (.stop (+ at duration 1.0)))))) ; Give a bit extra for the release

(def sine (partial oscillator "sine"))
(def sawtooth (partial oscillator "sawtooth"))
(def square (partial oscillator "square"))
(def triangle (partial oscillator "triangle"))


; Filters

(defn biquad-filter
  ([type freq q]
   (fn [context at duration]
     (section
       (doto (-> (biquad-filter type freq) (run-with context at duration) :output)
         (-> .-Q (plug q context at duration))))))
  ([type freq]
   (fn [context at duration]
     (section
       (doto (.createBiquadFilter context)
         (-> .-frequency .-value (set! 0))
         (-> .-frequency (plug freq context at duration))
         (-> .-type (set! type)))))))

(def low-pass (partial biquad-filter "lowpass"))
(def high-pass (partial biquad-filter "highpass"))


; Effects

(defn stereo-panner [pan]
  (fn [context at duration]
    (section
      (doto (.createStereoPanner context)
        (-> .-pan (plug pan context at duration))))))

(defn delay-line
  [time]
  (fn [context at duration]
    (let [maximum 5]
      (section
        (doto (.createDelay context maximum)
          (-> .-delayTime (plug time context at duration)))))))

; FIXME: Unify with noise.
(defn convolver
  [generate-bit!]
  (fn [context at duration]
    (let [sample-rate 44100
          frame-count (* sample-rate (+ duration 1.0)) ; Give a bit of extra for the release.
          buffer (.createBuffer context 1 frame-count sample-rate)
          data (.getChannelData buffer 0)]
      (doseq [i (range sample-rate)]
        (aset data i (generate-bit! i)))
      (section
        (doto (.createConvolver context)
          (-> .-buffer (set! buffer)))))))

(def wet
  (let [duration 5
        decay 3
        sample-rate 44100
        length (* sample-rate (+ duration 1.0))
        logarithmic-decay (fn [i]
                            (* (-> i (js/Math.random) (* 2.0) (- 1.0))
                               (Math/pow (- 1 (/ i length)) decay)))]
    (convolver logarithmic-decay)))

(defn reverb
  ([]
   (reverb 0.3))
  ([wetness]
   (add (gain 1.0) (connect-> wet (gain wetness)))))
