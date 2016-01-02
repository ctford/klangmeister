(ns klangmeister.editor
  (:require
    [klangmeister.actions :as action]
    [klangmeister.graph :as graph]
    [reagent.core :as reagent]))

(defn editor-did-mount [handle! _]
  (fn [this]
    (let [pane (.fromTextArea
                 js/CodeMirror
                 (reagent/dom-node this)
                 #js {:mode "clojure"
                      :theme "solarized"
                      :lineNumbers true
                      :matchBrackets true
                      :autoCloseBrackets true
                      :lineWrapping true})]
      (.on pane "change" #(-> % .getValue action/->Refresh handle!)))))

(defn render [handle! state]
  (reagent/create-class
    {:render (fn [] [:textarea {:default-value (:text state)
                                :auto-complete "off"}])
     :component-did-mount (editor-did-mount handle! state)}))
