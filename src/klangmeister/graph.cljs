(ns klangmeister.graph
  (:require
    [klangmeister.music :as music]
    [quil.core :as quil :include-macros true]
    [quil.middleware :as middleware]
    [reagent.core :as reagent]))

(defn scale [k to ms]
  (let [maximum (->> ms (map k) (apply max))
        minimum (->> ms (map k) (apply min))
        range (- maximum minimum)]
    (->> ms
         (map #(update % k - minimum))
         (map #(update % k / range))
         (map #(update % k * to)))))

(defn draw-graph [state-atom]
  (let [[height width] [150 800]
        [dot-height dot-width] [15 20]]
    (quil/sketch :draw (fn [_]
                         (try (quil/background 255)
                              (let [{:keys [music sync looping?]} @state-atom
                                    relative-time (-> (Date.now) (- sync) (mod (music/duration music)) (/ 1000))
                                    marked (map (fn [{:keys [time] :as note}]
                                                  (let [played? (and looping? (<= time relative-time))]
                                                    (assoc note :played? played?))) music)
                                    scaled (->> marked
                                                (scale :time (- width dot-width))
                                                (scale :pitch (- height dot-height)))]
                                (doseq [{:keys [time pitch played?]} scaled]
                                  (let [colour (if played? 200 20)
                                        half (partial * 0.5)
                                        [x y] [(+ time (half dot-width))
                                               (+ (- (+ pitch (half dot-height))) height)]]
                                    (quil/stroke colour)
                                    (quil/fill colour)
                                    (quil/ellipse x y dot-width dot-height))))
                              (catch js/Object e)))
                 :host "graph"
                 :no-start true
                 :middleware [middleware/fun-mode]
                 :size [width height])))

(defn graph [handle! state-atom]
  (reagent/create-class
    {:render (fn [] [:canvas#graph])
     :component-did-mount #(draw-graph state-atom)}))
