(ns klangmeister.editor
  (:require
    [klangmeister.actions :as action]
    [klangmeister.graph :as graph]
    [reagent.core :as reagent]))

(defn editor-did-mount [target handle!]
  (fn [this]
    (let [pane (.fromTextArea
                 js/CodeMirror
                 (reagent/dom-node this)
                 #js {:mode "clojure"
                      :theme "solarized"
                      :lineNumbers true
                      :matchBrackets true
                      :autoCloseBrackets true
                      :lineWrapping true
                      :viewportMargin js/Infinity})]
      (.on pane "change" #(-> % .getValue (action/->Refresh target) handle!)))))

(defn editor [target text handle!]
  (reagent/create-class
    {:render (fn []
               [:textarea {:default-value text
                           :auto-complete "off"
                           :class "text"}])
     :component-did-mount (editor-did-mount target handle!)}))

(defn render [target text handle! state]
  (let [{:keys [error]} (target state)]
    [:div
     {:class (str "editor" (if error " error" ""))}
     [editor target text handle!]
     [:div (some-> error .-cause .-message)]]))
