(defproject klangmeister "0.1.0-SNAPSHOT"
  :description "A musical scratchpad."
  :license {:name "MIT" }
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [reagent "0.5.1"]
                 [reagent-utils "0.1.7"]
                 [quil "2.3.0"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.5.3"]
                 [venantius/accountant "0.1.6"]
                 [org.clojure/clojurescript "1.7.228"]
                 [org.clojure/tools.namespace "0.3.0-alpha3"]
                 [org.clojure/tools.reader "1.0.0-alpha3"]
                 [org.clojure/java.classpath "0.2.3"]
                 [leipzig "0.10.0-SNAPSHOT"]]

  :min-lein-version "2.5.0"

  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-2"]
            [lein-doo "0.1.6"]]

  :clean-targets ^{:protect false} ["resources/public/klangmeister/js/compiled"
                                    "target"
                                    "out"]

  :source-paths ["src"]
  :resource-paths ["resources" "target/cljsbuild"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main "klangmeister.core"
                                   :optimizations :none
                                   :pretty-print true
                                   :output-to "resources/public/klangmeister/js/compiled/app.js"
                                   :output-dir "resources/public/klangmeister/js/compiled"
                                   :asset-path "js/compiled"}}
                       {:id "prod"
                        :source-paths ["src"]
                        :compiler {:main "klangmeister.core"
                                   :optimizations :simple
                                   :pretty-print false
                                   :optimize-constants true
                                   :static-fns true
                                   :output-to "resources/public/klangmeister/js/compiled/app.js"
                                   :asset-path "js/compiled"}}
                       {:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "out/testable.js"
                                   :output-dir "out"
                                   :optimizations :simple
                                   :main klangmeister.runner}}]}
  :figwheel {:css-dirs ["resources/public/klangmeister/css"]})
