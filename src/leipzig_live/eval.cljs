(ns leipzig-live.eval
  (:require
    [cljs.js :as cljs]))

(defn add-namespace [expr-str]
  (str
    "(ns leipzig-live.playing
      (:require [leipzig-live.music :as music]))"
    expr-str))

(defn uate
  [expr-str]
  (cljs/eval-str
    (cljs/empty-state)
    (add-namespace expr-str)
    nil
    {:eval cljs/js-eval
     :load (fn [_ cb] (cb {:lang :clj :source ""}))}
    identity))
