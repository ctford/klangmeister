(ns klangmeister.test.processing
  (:require [cljs.test :refer-macros [deftest is]]
            [klangmeister.processing :as processing]
            [klangmeister.framework :as framework]
            [klangmeister.actions :as action]))

(def ignore! (constantly nil))

(deftest stopping
  (is
    (=
     (framework/process (action/->Stop :foo) ignore! {:foo {:looping? true}})
     {:foo {:looping? false}})))
