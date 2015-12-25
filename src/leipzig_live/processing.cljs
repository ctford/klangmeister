(ns leipzig-live.processing
  (:require
    [leipzig-live.eval :as eval]
    [leipzig-live.music :as music]
    [leipzig-live.instruments :as instrument]
    [leipzig-live.actions :as action]
    [leipzig-live.framework :as framework]
    [cljs.js :as cljs]))

(extend-protocol framework/Action
  action/Refresh
  (process [{expr-str :text} _ {original-music :music :as state}]
    (let [{:keys [value error]} (eval/uate expr-str)
          music (or value original-music)]
      (-> state
          (assoc :error error)
          (assoc :text expr-str)
          (assoc :music music))))

  action/Stop
  (process [_ handle! state]
    (assoc state :looping? false))

  action/Play
  (process [this handle! state]
    (framework/process (action/->Loop) handle! (assoc state :looping? true)))

  action/Loop
  (process [this handle! {notes :music :as state}]
    (when (:looping? state)
      (music/play-on! instrument/beep! notes)
      (let [duration (->> notes (map :duration) (reduce +) (* 1000))]
        (js/setTimeout #(handle! this) duration)))
    state))
