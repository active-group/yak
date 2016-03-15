(ns yak.reader-test
  (:require [clojure.test :refer :all]
            [yak.reader :refer :all]))

(deftest process-content-test
  (is (= '([{:line 4, :column 3, :end-line 4, :end-column 31, :filename "test.clj"} (yak/L "Hello" "de" "Hallo")])
         (process-content "
(ns hello (:require [x :as y]))
(defn f []
  (yak/L \"Hello\" \"de\" \"Hallo\"))
"
                          "test.clj" 'yak/L))))

