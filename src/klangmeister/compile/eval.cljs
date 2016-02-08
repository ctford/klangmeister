(ns klangmeister.compile.eval
  (:require
    [cljs.js :as cljs])
  (:require-macros
    [klangmeister.compile.macros :as macro]))

(def namespace-declaration
  (macro/text "src/klangmeister/namespace.cljs.txt"))

(def dependencies
  (macro/sources
    leipzig.canon
    leipzig.chord
    leipzig.melody
    leipzig.scale
    leipzig.temperament
    klangmeister.sound.synthesis
    klangmeister.sound.instruments))

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
    (str namespace-declaration expr-str)
    nil
    {:eval cljs/js-eval
     :load loader}
    identity))
