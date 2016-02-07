(ns klangmeister.actions)

(defrecord Play [target])
(defrecord Loop [target])
(defrecord Stop [target])
(defrecord Refresh [text target])
