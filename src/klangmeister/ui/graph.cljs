(ns klangmeister.ui.graph
  (:require
    [leipzig.melody :as melody]
    [leipzig.scale :as scale]
    [leipzig.temperament :as temperament]
    [quil.core :as quil :include-macros true]
    [quil.middleware :as middleware]
    [reagent.core :as reagent]))

(defn scaler-for [to values]
  (let [maximum (apply max values)
        minimum (apply min values)
        spread (- maximum minimum)]
    (fn [x] (-> x (/ spread) (* to)))))

(defn translater-for [to values]
  (let [minimum (apply min values)]
    (fn [x] (-> x (- minimum)))))

(defn fitter-for [to values]
  (comp inc (scaler-for to values) (translater-for to values)))

(def guide-frequencies (range 0 128 2))

(defn draw-graph [state-atom]
  (let [[height width] [120 800]
        dot-ratio 1.618
        raw-dot-height 15]
    (quil/sketch :draw (fn []
                         (quil/background 255)
                         (let [{:keys [value sync looping?]} (:main @state-atom)
                               relative-time (-> (Date.now) (- sync) (mod (* 1000 (melody/duration value))) (/ 1000))
                               marked (->> value
                                           (melody/wherever
                                             #(and looping? (<= (:time %) relative-time))
                                             :played? (melody/is true)))
                               scale-dot (scaler-for (- height raw-dot-height) (map :pitch value))
                               dot-height (scale-dot 2)
                               dot-width (* dot-height 1.618)
                               fit-pitch (fitter-for (- height dot-height 2) (map :pitch value))
                               fit-time (fitter-for (- width dot-width 2) (map :time value))
                               half (partial * 0.5)
                               scaled (->> marked
                                           (melody/where :pitch fit-pitch)
                                           (melody/where :time fit-time))]
                           (doseq [pitch (map fit-pitch guide-frequencies)]
                             (let [y (+ height (- (+ pitch (half dot-height))))]
                               (quil/stroke 230)
                               (quil/line 0 y width y)))
                           (doseq [{:keys [time pitch played?]} scaled]
                             (let [colour (if played? 200 20)
                                   x (+ time (half dot-width))
                                   y (+ height (- (+ pitch (half dot-height))))]
                               (quil/stroke colour)
                               (quil/fill colour)
                               (quil/ellipse x y dot-width dot-height)))))
                 :host "graph"
                 :no-start true
                 :middleware [middleware/fun-mode]
                 :size [width height])))

(defn render [handle! state-atom]
  (reagent/create-class
    {:render (fn []
     [:div {:class "graph"} [:canvas#graph]])
     :component-did-mount #(draw-graph state-atom)}))
