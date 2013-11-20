(ns copycat-clojure.util-test
  (:use [clojure.test]
        [copycat-clojure.util]))


(deftest test-=ish
  (testing "default tol"
           (is (=ish 1 1.001))
           (is (=ish 1 0.999))
           (is (not (=ish 1 1.2)))
           (is (not (=ish 1 0.8)))
           )
  (testing "specified tol"
           (is (=ish 1 1.5 2))
           (is (=ish 1 0.6 2))
           (is (not (=ish 1 3 2)))
           (is (not (=ish 1 0.4 2)))
           )
  (testing "assert"
           (is (=ish 1 1 nil)) ;; (nil? TOL) is fine
           (is (thrown? (=ish 1 1 0))) ;; but TOL can't be 0
           (is (thrown? (=ish 1 1 -1))) ;; or negative
           )
  (testing "zero input"
           (is (=ish 0 0))
           (is (=ish 0 0.0))
           (is (not (=ish 0 1)))
           (is (not (=ish 1 0)))
  ))
  

(deftest test-all
  (testing "basic all"
           (is (all [1 2 3]))
           (is (all []))
           (is (all [1 2 []]))
           )
  (testing "false"
           (is (not (all [false])))
           (is (not (all [1 2 false])))
           (is (not (all [1 2 nil])))
           )
  (testing "can't be nil (following python)"
           (is (thrown? AssertionError (all nil)))
           )
  )


(deftest test-select-list-position
  (testing
   "select-list-position empty"
   (is (= (select-list-position nil)
          nil))
   (is (= (select-list-position [])
          nil)))
   (testing
    "select-list-position proportions-vec"
    (let [desired-propns [0.8 0.1 0.1]
          howmany 100000
          actual-val-propns (proportions-vec
                             (run-often select-list-position howmany desired-propns))
          actual-propns (map second actual-val-propns)
          ]
      (is (all (map (fn [[x y]] (=ish x y 1.2))
                    (partition 2 (interleave desired-propns actual-propns)))))
      ))
   )


(deftest test-weighted-average
  (testing "empty"
           (is (= (weighted-average nil)
                  0.0))
           (is (= (weighted-average [])
                  0.0))
           )
  (testing "basic"
           (is (= (weighted-average [[10 1]])
                  10.0))
           (is (= (weighted-average [[10 0.5] [20 0.5]])
                  15.0))
           )
  (testing "different weights"
           (is (= (weighted-average [[10 0.2] [20 0.8]])
                  (average [10.0 20 20 20 20])))
           (is (= (weighted-average [[10 0.2] [20 0.8]])
                  (average [10.0 20 20 20 20])))
           (is (= (weighted-average [[10 2] [20 8]])
                  (weighted-average [[10 0.2] [20 0.8]])))
           )
  (testing "invalid input"
           (is (thrown? AssertionError (weighted-average [[10 0]])))
           (is (thrown? AssertionError (weighted-average [[10 -1]])))
           )
  )


(deftest test-temperature-adjusted-value
  (testing "based on running the python code with sample values"
           (is (=ish (temperature-adjusted-value 100 10) 3.1622776601683795 1.000001))
           (is (=ish (temperature-adjusted-value 50 0) 0.0 1.000001))
           (is (=ish (temperature-adjusted-value 50 20) 659.0195889768269 1.000001))
           (is (=ish (temperature-adjusted-value 100 20) 4.47213595499958 1.000001))
           )


(deftest test-temperature-adjusted-probability
  (testing "based on running the python code with sample values"
           (is (=ish (temperature-adjusted-probability 100 0) 0 1.000001))
           (is (=ish (temperature-adjusted-probability 50 0) 0 1.000001))
           (is (=ish (temperature-adjusted-probability 50 20) 19.414213562373096 1.000001))
           (is (=ish (temperature-adjusted-probability 50 0.1) 0.12636038969321073 1.000001))
           ))


(deftest test-coin-flip
  (defn test-for-chance [chance]
    (let [howmany 10000]
      (is (=ish (:true (proportions-map (run-often coin-flip howmany chance)))
                chance))))
  (testing "basic"
           (test-for-chance 0.5)
           (test-for-chance 0.1)
           (test-for-chance 0.7)
           )
  (testing "exceptions"
           (is (thrown? AssertionError (coin-flip 0)))
           (is (thrown? AssertionError (coin-flip 1)))
           (is (thrown? AssertionError (coin-flip 2)))
           (is (thrown? AssertionError (coin-flip -1)))
           )
  )


