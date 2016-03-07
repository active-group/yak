(ns yak.writer-test
  (:require [clojure.test :refer :all]
            [yak.writer :refer :all]))

(deftest split-string-2-test
  (is (= ["abc\ndef" "ghi"]
         (split-string-2 "abc\ndefghi"
                         2 4)))
  (is (= ["abc\n" "def\nghi"]
         (split-string-2 "abc\ndef\nghi"
                         2 1))))

(deftest split-string-3-test
  (is (= ["abc\ndef" "ghi" "xyz"]
         (split-string-3 "abc\ndefghixyz"
                         2 4 2 6)))
  (is (= ["abc\n" "def\nghi" "xyz"]
         (split-string-3 "abc\ndef\nghixyz"
                         2 1 3 3))))

(deftest replace-entry-test
  (is (= "abc\n(hello)xyz"
         (replace-entry "abc\ndef\nghixyz"
                        [{:line 2 :column 1 :end-line 3 :end-column 3}
                         '(hello)]))))
