(ns leipzig-live.framework)

(defprotocol Action
  (process [this handle! state]))

(declare handler-for)

(defn apply-action! [state-atom action]
  (swap!
    state-atom
    (fn [state] (process action (handler-for state-atom) state))))

(defn handler-for [state-atom]
  (partial apply-action! state-atom))
