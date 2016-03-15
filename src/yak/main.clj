(ns yak.main
  "Main utilities to extract localizable strings from Clojure and ClojureScript files, and to merge translations back in."
  (:require [yak.analyze :as analyze]
            [yak.reader :as reader]
            [yak.writer :as writer]
            [yak.edn :as edn]))

(defn write-back!
  "Write back the given entries to the files relative to the current dir."
  [entries & [encoding]]
  (-> entries
      (analyze/sort-entries)
      (writer/write-back-entries! encoding)))

(defn fill-up!
  [dir id & [encoding]]
  ;; Note: encoding is both input and output encoding
  (-> (reader/read-entries-in dir id encoding)
      (analyze/fill-up)
      (write-back! encoding)))

(defn largest-missing-edn
  "Write to `out-file` in edn format, the largest localizable entries
  from `dir` that still miss a translation for `locale`"
  [out-file dir id locale & [encoding]]
  (-> (reader/read-entries-in dir id encoding)
      (analyze/filter-largests)
      (analyze/filter-missing locale)
      (edn/write-entries! out-file encoding)))

(defn write-back-edn!
  "Read back translations from `in-file` in edn format and write them
  back to the files."
  [in-file & [encoding]]
  (-> (edn/read-entries in-file encoding)
      (write-back! encoding)))

(defn write-back-edn-and-fill-up!
  "Read back translations from `in-file` in edn format, write them
  back, and then fill up and write back all `smaller` duplicates of
  any translation (not only those written back)."
  [in-file dir id & [encoding]]
  (write-back-edn! in-file encoding)
  (fill-up! dir id encoding))
