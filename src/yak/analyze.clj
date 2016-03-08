(ns yak.analyze
  (:require [clojure.data :as data])
  )

(defn entry-key
  "A key for an entry, which is map of all translations of the form."
  [entry]
  (let [[location [id dflt & rest]] entry]
    (into {nil dflt}
          (map (fn [[l txt]]
                 [l txt])
               (partition 2 rest)))))

(defn entry>= [ent1 ent2]
  (let [k1 (entry-key ent1)
        k2 (entry-key ent2)
        [only-in-1 only-in-2 both] (data/diff k1 k2)]
    ;; ent1 is larger, if ent2 has nothing 'on its own'.
    (empty? only-in-2)))

(defn remove-smaller
  "Remove those from `entries`, that are smaller or equal to `entry`."
  [entries entry]
  (remove #(entry>= entry %) entries))

(defn any-larger?
  [entries entry]
  (some #(entry>= % entry) entries))

(defn filter-largests
  "From the given entries, return only the largests."
  [entries]
  (reduce (fn [res entry]
            (if (any-larger? res entry)
              res
              (conj (remove-smaller res entry) entry)))
          []
          entries))

(defn permutations [s]
  (lazy-seq
   (if (seq (rest s))
     (apply concat (for [x s]
                     (map #(cons x %) (permutations (remove #{x} s)))))
     [s])))

(defn unique-permutations [coll]
  (if (empty? (rest coll)) ;; only non-empty for us here
    (set [(set coll)])
    (let [r (unique-permutations (rest coll))]
      (set (concat r
                   [(set [(first coll)])]
                   (map #(conj % (first coll))
                        r))))))

(defn all-keys [entry]
  (let [k (entry-key entry)]
    (map (fn [keys]
           (into {} (map #(vector % (get k %))
                         keys)))
         ;; don't need the ones that don't contain a default (not possible as key)
         (filter #(contains? % nil)
                 (unique-permutations (keys k))))))

(defn lookup-by-all-keys
  "For all smaller keys for all entries, return a map mapping to one of those entries."
  ;; Note: for duplicates like (diverging translations), this just returns one of them.
  [entries]
  (apply merge
         (map (fn [entry]
                (into {} (map vector
                              (all-keys entry)
                              (repeat entry))))
              entries)))

(defn fill-up
  "Fill up all entries to the `largest` duplicate in the list,
  meaning the one that has the same texts, or more."
  [entries]
  (let [largest (filter-largests entries)
        lookup (lookup-by-all-keys largest)]
    (map (fn [entry]
           ;; replace the value, possibly (but not the key!)
           (let [[k v] entry]
             [k (second (get lookup (entry-key entry) entry))]))
         entries)))

(defn sort-entries
  [entries]
  (sort-by (fn [[{:keys [filename line column]} v]]
             ;; sort by filename, and inverse occurence in file.
             [filename (- line) (- column)])
           entries))

(defn filter-missing
  "Return only those entries, that do not have a translation for the given locale yet."
  [entries locale]
  (remove #(contains? (set (keys (entry-key %))) locale)
          entries))
