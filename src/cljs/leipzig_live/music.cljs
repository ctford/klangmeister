(ns leipzig-live.music)

(defn play-on! [instrument! durations pitches]
  (let [times (reductions + 0 durations)]
    (doseq [[freq start duration] (map vector pitches times durations)]
      (instrument! freq start duration))))
