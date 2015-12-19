(ns leipzig-live.music)

(defn phrase [durations pitches]
  (let [times (reductions + 0 durations)]
    (map vector pitches times durations)))

(defn play-on! [instrument! durations pitches]
  (let [notes (phrase durations pitches)]
    (doseq [[freq start duration] notes]
      (instrument! freq start duration))))
