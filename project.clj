(defproject leipzig-live "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [reagent "0.5.1"]
                 [org.clojure/clojurescript "1.7.170" :scope "provided"]]

  :plugins [[lein-cljsbuild "1.1.1"]]

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj"]
  :resource-paths ["resources" "target/cljsbuild"]

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler {:output-to "resources/public/js/app.js"
                                        :output-dir "resources/public/js/out"
                                        :asset-path   "js/out"
                                        :optimizations :none
                                        :pretty-print  true}}}}

  :profiles {:dev {:repl-options {:init-ns leipzig-live.repl}

                   :dependencies [[lein-figwheel "0.5.0-2"]
                                  [org.clojure/clojurescript "1.7.170"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [pjstadig/humane-test-output "0.7.0"]]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.0-2"]
                             [org.clojure/clojurescript "1.7.170"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :figwheel {:http-server-root "public"
                              :server-port 3449
                              :nrepl-port 7002
                              :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
                              :css-dirs ["resources/public/css"]}

                   :env {:dev true}

                   :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]
                                              :figwheel {:on-jsload "leipzig-live.core/reload!"}
                                              :compiler {:main "leipzig-live.dev"
                                                         :source-map true}}}}}})
