(ns leipzig-live.framework)

(defprotocol Action
  (process [this state]))

(defn apply-action! [state-atom action]
  (swap! state-atom (partial process action)))
