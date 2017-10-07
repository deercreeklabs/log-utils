(ns deercreeklabs.doo-test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [deercreeklabs.log-utils-test]))

(enable-console-print!)

(doo-tests 'deercreeklabs.log-utils-test)
