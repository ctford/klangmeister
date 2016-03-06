(ns klangmeister.test.processing
  (:require [cljs.test :refer-macros [deftest testing is]]
            [klangmeister.processing :as processing]
            [klangmeister.framework :as framework]
            [klangmeister.compile.eval :as eval]
            [klangmeister.actions :as action]))

(def ignore! (constantly nil))

(deftest stopping
  (testing
    (is
      (=
       (framework/process (action/->Stop :foo) ignore! {:foo {:looping? true}})
       {:foo {:looping? false}}))
    (is
      (=
       (framework/process (action/->Stop :foo) ignore! {:foo {:looping? false}})
       {:foo {:looping? false}}))))

(deftest refreshing
  (testing
    (is
      (=
       (framework/process (action/->Refresh "(phrase [1] [69])" :foo) ignore! {})
       {:foo {:value [{:time 0 :pitch 69 :duration 1}] :text "(phrase [1] [69])" :error nil}}))
    (is
      ((comp :error :foo)
       (framework/process (action/->Refresh "(phrase [1] [6" :foo) ignore! {})))))
