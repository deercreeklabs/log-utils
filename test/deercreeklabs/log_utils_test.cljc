(ns deercreeklabs.log-utils-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [deercreeklabs.log-utils :as lu]
   [schema.core :as s :include-macros true]
   [schema.test :as st]
   [taoensso.timbre :as timbre :refer [debugf errorf infof]]))

(use-fixtures :once st/validate-schemas)

(deftest test-sym-map
  (let [a 1
        b 2]
    (is (= {:a 1 :b 2}
           (lu/sym-map a b)))))
