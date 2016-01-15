(ns klangmeister.macros
  (:require [clojure.tools.namespace.find :as find]
            [clojure.tools.namespace.file :as file]
            [clojure.tools.reader :as reader]
            [clojure.java.io :as io]
            [clojure.java.classpath :as classpath])
  (:import (java.io File)))

(defn name-val [rdr]
  ((juxt (comp str second file/read-file-ns-decl) slurp) rdr))

(defn files []
  (->> (classpath/classpath-directories)
       (map io/file)
       (filter #(.exists ^File %))
       (mapcat #(find/find-sources-in-dir % find/cljs))))

(defn collate [entries]
  (reduce conj {} entries))

(defn sources* [names]
  (let [in-names? (->> names (map str) set)
        relevant? (fn [[name _]] (in-names? name))]
    (->> (files)
         (map name-val)
         (filter relevant?)
         collate)))

(defmacro sources [& names]
  (sources* names))
