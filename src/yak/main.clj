(ns yak.main
  "Main utilities to extract localizable strings from Clojure and ClojureScript files, and to merge translations back in."
  (:require [yak.core :as core]
            [yak.locale :as locale]
            [yak.reader :as reader]))

(comment (yak/L "Hello" "de" "Hallo"))

#_(defn complete-duplicates
  [dir id locale]

  )
