(defproject leipzig-live "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [reagent "0.5.1"]
                 [org.clojure/clojurescript "1.7.170"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-2"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"]

  :source-paths ["src"]

  :cljsbuild {:builds {:dev {:source-paths ["src"]
                             :figwheel true
                             :compiler {:main "leipzig-live.core"
                                        :optimizations :none
                                        :output-to "resources/public/js/compiled/app.js"
                                        :output-dir "resources/public/js/compiled"
                                        :asset-path "js/out"}}
                       :prod {:source-paths ["src"]
                              :compiler {:optimizations :advanced
                                         :output-to "resources/public/js/compiled/app.js"
                                         :asset-path "js/out"}}}})
