(ns yak.writer
  (:require [clojure.java.io :as io]))

(defn find-char-pos [s line column]
  (loop [s s
         line line
         res 0]
    (assert (> line 0))
    (if (= line 1)
      (+ res (dec column))
      (let [[l r] (split-with #(not= % \newline) s)]
        (recur (rest r) ;; also drop the nl
               (dec line)
               (+ res 1 (count l)))))))

(defn split-string-3 [s line column end-line end-column]
  (let [p1 (find-char-pos s line column)
        p2 (find-char-pos s end-line end-column)]
    [(subs s 0 p1) (subs s p1 p2) (subs s p2)]))

(defn replace-string [s {:keys [line column end-line end-column]} r]
  (let [[before _ after] (split-string-3 s line column end-line end-column)]
    (str before r after)))

(defn replace-entry [s [pos ent]] ;; TODO: no need for locale to-string?
  (replace-string s pos (pr-str ent)))

(defn replace-entries [s entries]
  ;; Warn: can only work if entries are sorted from end to beginning of that file.
  (reduce replace-entry
          s
          entries))

(defn write-back-entries! [entries & [encoding]]
  (let [by-f (group-by #(:filename (first %))
                       entries)]
    (doseq [[f entries] by-f]
      ;;(println "Processing" (.getPath (io/file f)))
      (let [ff (io/file f)
            before (slurp ff :encoding encoding)
            after (-> before
                      (replace-entries entries))]
        (when (not= before after)
          (println "Writing" (.getPath ff))
          (spit ff after))))))
