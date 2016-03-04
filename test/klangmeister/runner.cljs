(ns klangmeister.runner
  (:require [doo.runner :refer-macros [doo-all-tests]]
            [klangmeister.example :refer []]))

(enable-console-print!)

(doo-all-tests)
