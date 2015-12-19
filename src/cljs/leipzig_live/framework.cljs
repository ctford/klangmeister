(ns leipzig-live.framework)

(defprotocol Action
  (process [this handle! state]))

(defn apply-action! [state-atom action]
  (swap! state-atom (partial process action (partial apply-action! state-atom))))
