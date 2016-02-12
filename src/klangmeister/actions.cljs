(ns klangmeister.actions)

(defrecord Play [target])
(defrecord PlayOnce [target])
(defrecord Test [target])
(defrecord Loop [target])
(defrecord Stop [target])
(defrecord Refresh [text target])
