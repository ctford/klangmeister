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
                      :lineWrapping true})]
      (.on pane "change" #(-> % .getValue (action/->Refresh target) handle!)))))

(defn editor [target handle! state]
  (reagent/create-class
    {:render (fn []
               [:textarea {:default-value (-> state target :text)
                           :auto-complete "off"
                           :class "text"}])
     :component-did-mount (editor-did-mount target handle!)}))

(defn render [target handle! state]
  (let [{:keys [error]} (target state)]
    [:div
     {:class (str "editor" (if error " error" ""))}
     [editor target handle! state]
     [:div (some-> error .-cause .-message)]]))
