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
    (fn [x] (-> x (- minimum) (/ spread) (* to)))))

(def guide-frequencies (range 0 128 2))

(defn draw-graph [state-atom]
  (let [[height width] [120 800]
        [dot-height dot-width] [15 20]]
    (quil/sketch :draw (fn [_]
                         (try (quil/background 255)
                              (let [{:keys [music sync looping?]} (:main @state-atom)
                                    relative-time (-> (Date.now) (- sync) (mod (* 1000 (melody/duration music))) (/ 1000))
                                    marked (->> music
                                                (melody/wherever
                                                  #(and looping? (<= (:time %) relative-time))
                                                  :played? (melody/is true)))
                                    scale-pitch (scaler-for (- height dot-height) (map :pitch music))
                                    scale-time (scaler-for (- width dot-width) (map :time music))
                                    half (partial * 0.5)
                                    scaled (->> marked
                                                (melody/where :pitch scale-pitch)
                                                (melody/where :time scale-time))]
                                (doseq [pitch (map scale-pitch guide-frequencies)]
                                  (let [y (+ height (- (+ pitch (half dot-height))))]
                                    (quil/stroke 230)
                                    (quil/line 0 y width y)))
                                (doseq [{:keys [time pitch played?]} scaled]
                                  (let [colour (if played? 200 20)
                                        x (+ time (half dot-width))
                                        y (+ height (- (+ pitch (half dot-height))))]
                                    (quil/stroke colour)
                                    (quil/fill colour)
                                    (quil/ellipse x y dot-width dot-height))))
                              (catch js/Object e)))
                 :host "graph"
                 :no-start true
                 :middleware [middleware/fun-mode]
                 :size [width height])))

(defn render [handle! state-atom]
  (reagent/create-class
    {:render (fn []
     [:div {:class "graph"} [:canvas#graph]])
     :component-did-mount #(draw-graph state-atom)}))
