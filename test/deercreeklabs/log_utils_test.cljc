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

(deftest test-ex-msg
  (let [msg "testing"
        e (ex-info msg {})]
    (is (clojure.string/includes? (lu/ex-msg e) msg))))

(deftest test-current-time-ms
  (is (number? (lu/current-time-ms))))
