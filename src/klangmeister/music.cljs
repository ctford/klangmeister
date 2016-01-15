(ns klangmeister.music
  (:require [leipzig.temperament :as temperament]
            [klangmeister.instruments :as instrument]))

(def instruments
  {:omg instrument/omg!
   :bop instrument/bop!
   :bell instrument/bell!
   :fuzz instrument/fuzz!
   :buzz instrument/buzz!})

(defn play-on! [notes]
  (doseq [{:keys [time instrument] :as note} notes]
    (let [synth! (get instruments instrument instrument/bell!)
          at (+ time (.-currentTime instrument/context)) ]
      (-> note
          (update :pitch temperament/equal)
          (dissoc :time)
          synth!
          (apply [at instrument/context])))))
