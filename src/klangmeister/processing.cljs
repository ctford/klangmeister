(ns klangmeister.processing
  (:require
    [klangmeister.eval :as eval]
    [klangmeister.music :as music]
    [klangmeister.instruments :as instrument]
    [klangmeister.actions :as action]
    [klangmeister.framework :as framework]
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
    (let [new-state (-> state
                        (assoc :looping? true)
                        (assoc :duration (-> state :music music/duration)))]
      (framework/process (action/->Loop) handle! new-state)))

  action/Loop
  (process [this handle! {:keys [music looping?] :as state}]
    (if looping?
      (let [start (Date.now)]
        (music/play-on! instrument/bell! music)
        (js/setTimeout #(handle! this) (music/duration music))
        (assoc state :sync start))
      (dissoc state :sync))))
