(defproject leipzig-live "0.1.0-SNAPSHOT"
  :description "A musical scratchpad."
  :license {:name "MIT" }
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [reagent "0.5.1"]
                 [quil "2.3.0"]
                 [org.clojure/clojurescript "1.7.170"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-2"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"]
  :resource-paths ["resources" "target/cljsbuild"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main "leipzig-live.core"
                                   :optimizations :none
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled"
                                   :asset-path "js/compiled"}}
                       {:id "prod"
                        :source-paths ["src"]
                        :compiler {:optimizations :simple
                                   :output-to "resources/public/js/compiled/app.js"
                                   :asset-path "js/compiled"}}]}

  :figwheel {:css-dirs ["resources/public/css"]})
