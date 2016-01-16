(ns klangmeister.eval
  (:require
    [cljs.js :as cljs])
  (:require-macros
    [klangmeister.macros :as macro]))

(defn print-error [error]
  (when-let [cause (.-cause error)]
    (println (.-message cause))
    (println (.-stack cause))))

(def dependencies
  (macro/sources
    leipzig.canon
    leipzig.chord
    leipzig.melody
    leipzig.scale
    leipzig.temperament
    klangmeister.instruments))

(defn loader [{:keys [name]} callback]
  (let [str-name (.-str name)
        source (dependencies str-name)
        source nil]
    (if source
      (do (js/console.log (str "Loading " str-name "."))
          (callback {:lang :clj :source source}))
      (do
        (js/console.log (str "Unable to load " str-name "."))
        (callback {:lang :clj :source ""})))))

(defonce state (cljs/empty-state))

(defn uate
  [expr-str]
  (cljs/eval-str
    state
    expr-str
    nil
    {:eval cljs/js-eval
     :load loader}
    identity))
