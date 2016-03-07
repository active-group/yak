(ns yak.writer
  
  )

(defn split-string-2' [s line column accu]
  (if (= line 1)
    (let [[bef aft] (split-at (dec column) s)]
      [(apply str (concat accu bef)) (apply str aft)])
    (let [[p1 r1] (split-with #(not= % \newline) s) ;; consider CRLF?
          [nl r2] (split-at 1 r1)]
      (recur r2 (dec line) column (concat accu p1 nl)))))

(defn split-string-2 [s line column]
  (split-string-2' s line column nil))

(defn split-string-3 [s line column end-line end-column]
  ;; Leaves some room for optimization:
  (let [[bef r] (split-string-2 s line column)
        [m aft] (split-string-2 r
                                (+ 1 (- end-line line))
                                ;; end-column is inclusive:
                                (+ 1 (if (= end-line line)
                                       (- end-column column)
                                       end-column)))]
    [bef m aft]))

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

(defn write-back-entries [entries]
  (let [by-f (group-by #(:filename (first %))
                       entries)]
    (doseq [[f entries] by-f]
      ;; TOOO: encodings
      (spit f (-> (slurp f)
                  (replace-entries entries))))))
