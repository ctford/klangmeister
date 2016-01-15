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
    leipzig.melody))

(defn build-namespace [expr-str]
  (str
    "(ns leipzig-live.playing
       (:require [leipzig.scale :as scale]
                 [leipzig.melody :as melody]))"
    expr-str))

(defn uate
  [expr-str]
  (cljs/eval-str
    (cljs/empty-state)
    (build-namespace expr-str)
    nil
    {:eval cljs/js-eval
     :load (fn [{:keys [name]} callback]
             (callback {:lang :clj :source (get dependencies name "")}))}
    identity))
