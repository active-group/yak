(ns yak.core-test
  (:require [clojure.test :refer :all]
            [yak.core :refer :all]))

(deftest L-test
  (is (= (L "abc")
         (L "abc")))
  (is (not= (L "abc")
            (L "def")))
  (is (not= (L "abc")
            (L "Hello" "de" "Hallo"))))

(deftest uni-test
  (is (= (uni "abc")
         (L "abc"))))

(deftest >>-test
  (is (= (>> (L "abc") (L "def"))
         (>> (L "abc") (L "def"))))
  (is (not= (>> (L "def") (L "abc"))
            (>> (L "abc") (L "def")))))

(deftest localize-test
  (is (= "abc"
         (localize (L "abc") "de")))
  (is (= "abc"
         (localize (L "abc") "en_US")))
  (is (= "def"
         (localize (L "abc" "de" "def") "de")))
  (is (= "def"
         (localize (L "abc" "de" "def") "de_DE"))))

