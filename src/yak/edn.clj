(ns yak.edn
  "Utilities to read and write entity lists in an edn format."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn write-entries! [entries f & [encoding]]
  (spit f
        (with-out-str
          (doseq [e entries]
            (println (pr-str e))))
        :encoding encoding))

(defn read-entries [f & [encoding]]
  (with-open [in (io/reader f)]
    (let [pin (new java.io.PushbackReader in)]
      (doall (loop [res []]
               (if-let [v (edn/read {:eof nil} pin)]
                 (recur (conj res v))
                 res))))))
