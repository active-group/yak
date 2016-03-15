(ns yak.csv
  (:require [yak.core :as core]))

(defn defs-in-L [ent]
  (into (array-map)
        (cons [nil (second ent)]
              (apply array-map (rest (rest ent))))))

(defn locales-in-L [ent]
  (keys (defs-in-L ent)))

(defn locale-repr [l]
  (if (nil? l)
    ""
    (core/-to-string l)))

(defn pos-repr [{:keys [filename line column end-line end-column]}]
  (str filename " " line " " column " " end-line " " end-column))

(defn csv [entries]
  (let [locales' (mapcat (comp locales-in-L second) entries)
        locales (sort (distinct locales'))]
    (cons (concat [""]
                  (map locale-repr locales))
          (map (fn [[pos ent]]
                 (let [mp (defs-in-L ent)]
                   (cons (pos-repr pos)
                         (map #(get mp % nil)
                              locales))))
               entries))))


