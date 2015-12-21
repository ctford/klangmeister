(ns leipzig-live.framework)

(defprotocol Action
  "Calculate the new state and `handle!` any new Actions that arise."
  (process [this handle! state]))

(declare handler-for)

(defn apply-action!
  "Update the atom using the action."
  [state-atom action]
  (swap!
    state-atom
    (fn [state] (process action (handler-for state-atom) state))))

(defn handler-for
  "Build a `handle!` function for the atom."
  [state-atom]
  (partial apply-action! state-atom))
