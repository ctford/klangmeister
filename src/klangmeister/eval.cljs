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
    leipzig.scale
    leipzig.melody
    klangmeister.live))

(defn loader [{:keys [name]} callback]
  (callback {:lang :clj :source (get dependencies name "")}))

(defn uate
  [expr-str]
  (cljs/eval-str
    (cljs/empty-state)
    expr-str
    nil
    {:eval cljs/js-eval
     :load loader}
    identity))
