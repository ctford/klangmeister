(ns klangmeister.test.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [klangmeister.test.processing]))

(enable-console-print!)

(doo-tests 'klangmeister.test.processing)
