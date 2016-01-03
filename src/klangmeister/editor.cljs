(ns klangmeister.editor
  (:require
    [klangmeister.actions :as action]
    [klangmeister.graph :as graph]
    [reagent.core :as reagent]))

(defn editor-did-mount [handle!]
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

(defn editor [handle! {:keys [text]}]
  (reagent/create-class
    {:render (fn []
               [:textarea {:default-value text
                           :auto-complete "off"}])
     :component-did-mount (editor-did-mount handle!)}))

(defn render [handle! {:keys [error] :as state}]
  [:div
   {:class (str "editor" (if error " error" ""))}
   [editor handle! state]])
