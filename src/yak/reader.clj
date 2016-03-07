(ns yak.reader
  (:require [clojure.tools.reader :as r]
            [clojure.tools.reader.reader-types :as rt]
            [clojure.java.io :as io]))

(defn all-ls [v id]
  (if (coll? v)
    (if (and (= id (first v))
             (string? (second v)))
      [v]
      (mapcat #(all-ls % id) v))
    nil))

(defn read-ls
  "Read lisp code in string s, returning all forms starting with `id`
  The forms are returned in the order they appear in a depth-first
  recursion into subforms. There will
  be :line, :column, :end-line, :end-column meta data attached to
  each."
  [s id]
  (binding [r/*read-eval* false
            ;; unless a default-data-reader-fn is set, set it to
            ;; ignore all unknown tags (will be called for #js for
            ;; example)
            r/*default-data-reader-fn* (or r/*default-data-reader-fn*
                                           (fn [tag value]
                                             nil))]
    (let [reader (rt/indexing-push-back-reader s)]
      (loop [res []]
        (let [v (r/read {:read-cond :preserve
                         :eof ::eof}
                        reader)]
          (if (= ::eof v)
            res
            (recur (apply conj res (all-ls v id)))))))))

(defn add-meta-position
  [v filename]
  [(-> (meta v) ;; :line :column :end-line :end-column
       (assoc :filename filename))
   v])

(defn add-meta-positions
  [coll filename]
  (map #(add-meta-position % filename)
       coll))

(defn sort-entries
  [entries]
  (sort-by (fn [[{:keys [filename line column]} v]]
             ;; sort by filename, and inverse occurence in file.
             [filename (- line) (- column)])
           entries))

(defn process-content [s filename id]
  (-> s
      (read-ls id)
      (add-meta-positions filename)
      (sort-entries)))

(defn read-file [file id & [encoding]]
  (println "Reading" (.getPath (io/file file)))
  (let [r (-> (slurp file :encoding (or encoding "utf-8"))
              (process-content (.getPath (io/file file)) id))]
    (println "  found" (count r) "localizables.")
    r))

(defn read-files-in [dir id & [encoding]]
  (mapcat #(read-file % id encoding)
          (filter (fn [^java.io.File f]
                    (and (.isFile f)
                         (not (.isHidden f))
                         (let [^String n (.toLowerCase (.getName f))]
                           (or (.endsWith n ".clj")
                               (.endsWith n ".cljs")
                               (.endsWith n ".cljc")
                               (.endsWith n ".cljx")))))
                  (file-seq (io/file dir)))))
