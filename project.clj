(defproject klangmeister "0.1.0-SNAPSHOT"
  :description "A musical scratchpad."
  :license {:name "MIT" }
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [reagent "0.5.1"]
                 [reagent-utils "0.1.7"]
                 [quil "2.3.0"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.1.6"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/tools.namespace "0.3.0-alpha3"]
                 [org.clojure/tools.reader "1.0.0-alpha2"]
                 [org.clojure/java.classpath "0.2.3"]
                 [leipzig "0.10.0-SNAPSHOT"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-2"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"]
  :resource-paths ["resources" "target/cljsbuild"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main "klangmeister.core"
                                   :optimizations :none
                                   :pretty-print true
                                   :parallel-build true
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled"
                                   :asset-path "js/compiled"}}
                       {:id "prod"
                        :source-paths ["src"]
                        :compiler {:optimizations :simple
                                   :pretty-print false
                                   :optimize-constants true
                                   :static-fns true
                                   :output-to "resources/public/js/compiled/app.js"
                                   :asset-path "js/compiled"}}]}

  :figwheel {:css-dirs ["resources/public/css"]})
