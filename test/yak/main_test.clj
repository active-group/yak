(ns yak.main-test
  (:require [clojure.test :refer :all]
            [yak.main :refer :all]))

(deftest process-content-test
  (is (= '([["test.clj" 4 3] (yak/L "Hello" "de" "Hallo")])
         (process-content "
(ns hello (:require [x :as y]))
(defn f []
  (yack/L \"Hello\" \"de\" \"Hallo\"))
" "test.clj"))))

(deftest defs-in-L-test
  (is (= {nil "Hello" "de" "Hallo"}
         (defs-in-L '(yak/L "Hello" "de" "Hallo")))))

(deftest locales-in-L-test
  (is (= [nil "de"]
         (locales-in-L '(yak/L "Hello" "de" "Hallo")))))
