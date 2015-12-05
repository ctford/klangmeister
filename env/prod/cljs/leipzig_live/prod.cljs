(ns leipzig-live.prod
  (:require [leipzig-live.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
