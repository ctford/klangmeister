(ns klangmeister.test.runner
  (:require [doo.runner :refer-macros [doo-all-tests]]
            [klangmeister.test.processing]))

(enable-console-print!)

(doo-all-tests)
