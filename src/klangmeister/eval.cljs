(ns klangmeister.eval
  (:require
    [cljs.js :as cljs])
  (:require-macros
    [klangmeister.macros :as macro]))

(def dependencies
  (macro/sources
    leipzig.canon
    leipzig.chord
    leipzig.melody
    leipzig.scale
    leipzig.temperament
    klangmeister.synthesis))

(defn loader [{:keys [name]} callback]
  (let [str-name (.-str name)
        source (dependencies str-name)]
    (if source
      (do (js/console.log (str "Loading " str-name "."))
          (callback {:lang :clj :source source}))
      (do
        (js/console.log (str "Unable to load " str-name "."))
        (callback {:lang :clj :source ""})))))

(def state (cljs/empty-state))

(set-print-err-fn! #(js/console.log))

(defn uate
  [expr-str]
  (cljs/eval-str
    state
    expr-str
    nil
    {:eval cljs/js-eval
     :load loader}
    identity))
