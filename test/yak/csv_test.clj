(ns yak.csv-test
  (:require [clojure.test :refer :all]
            [yak.csv :refer :all]))

(deftest csv-test
  (is (= '(("" "" "de") ("test.clj 4 3" "Hello" "Hallo"))
         (csv '([["test.clj" 4 3] (yak/L "Hello" "de" "Hallo")])))))
