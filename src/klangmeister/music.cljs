(ns klangmeister.music
  (:require [leipzig.temperament :as temperament]
            [klangmeister.instruments :as instrument]))

(def leipzig "
(defn note [time pitch duration] {:time time :pitch pitch :duration duration})
(defn where [k f notes] (->> notes (map #(update-in % [k] f))))
(defn from [offset] (partial + offset))

(defn sum-n [series n] (reduce + (take n series)))

(defn scale [intervals]
  (fn [degree]
    (if-not (neg? degree)
      (sum-n (cycle intervals) degree)
      ((comp - (scale (reverse intervals)) -) degree))))

(def major (scale [2 2 1 2 2 2 1]))

; alternative scales
(def minor (scale [2 1 2 2 1 2 2]))
(def blues (scale [3 2 1 1 3 2]))
(def pentatonic (scale [3 2 2 3 2]))
(def chromatic (scale [1]))

(def A (from 69))
(def B (from 71))
(def C (from 72))
(def D (from 74))
(def E (from 76))
(def F (from 77))
(def G (from 79))

(def sharp inc)
(def flat dec)

(defn bpm [beats] (fn [beat] (/ (* beat 60) beats)))

(defn phrase [durations pitches]
  (let [times (reductions + 0 durations)]
    (map note times pitches durations)))

(defn- before? [a b] (<= (:time a) (:time b)))
(defn with
  ([[a & other-as :as as] [b & other-bs :as bs]]
   (cond
     (empty? as) bs
     (empty? bs) as
     (before? a b) (cons a (lazy-seq (with other-as bs)))
     :otherwise    (cons b (lazy-seq (with as other-bs)))))
  ([as bs & others] (reduce with (cons as (cons bs others)))))

(defn after [wait notes]
  (where :time (from wait) notes))

(defn duration
  [notes]
  (let [length (fn [{:keys [time duration]}] (+ time duration))]
    (->> notes
         (map length)
         (reduce max 0))))

(defn then [later earlier]
  (->> later
       (after (duration earlier))
       (with earlier)))

(def is constantly)")

(def instruments
  {:bell instrument/bell!
   :fuzz instrument/fuzz!
   :buzz instrument/buzz!})

(defn play-on! [notes]
  (doseq [{:keys [instrument] :as note} notes]
    (let [synth! (get instruments instrument instrument/bell!)]
      (-> note (update :pitch temperament/equal) synth!))))
