(ns yak.csv-test
  (:require [clojure.test :refer :all]
            [yak.csv :refer :all]))

(deftest csv-test
  (is (= '(("" "" "de") ("test.clj 4 3 4 31" "Hello" "Hallo"))
         (csv '([{:line 4, :column 3, :end-line 4, :end-column 31, :filename "test.clj"} (yak/L "Hello" "de" "Hallo")])))))

(deftest defs-in-L-test
  (is (= {nil "Hello" "de" "Hallo"}
         (defs-in-L '(yak/L "Hello" "de" "Hallo")))))

(deftest locales-in-L-test
  (is (= [nil "de"]
         (locales-in-L '(yak/L "Hello" "de" "Hallo")))))
