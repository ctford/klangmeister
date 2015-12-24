(ns leipzig-live.music)

(defn note [pitch time duration] {:pitch pitch :time time :duration duration})
(defn phrase [durations pitches]
  (let [times (reductions + 0 durations)]
    (map note pitches times durations)))

(defn play-on! [instrument! notes]
  (doseq [{:keys [pitch time duration]} notes]
    (instrument! pitch time duration)))
