(ns yak.core
  "Internationalization and localization in Clojure and ClojureScript code."
  ;;(:require)
  )

(defprotocol Locale
  (-reduce [this] "Return the next more basic locale for this, or nil if this is the root locale.")
  (-to-string [this] "Return a string representation of this.")
  (-from-string [this s] "Return the locale from the string representation returned by -to-string."))

(defn- localize-1 [mp locale]
  (assert (map? mp))
  (assert locale)
  (if (contains? mp locale)
    (get mp locale)
    (if-let [next (-reduce locale)]
      (localize-1 mp next)
      (do
        (assert (contains? mp nil) (str "No default in localizable: " (pr-str mp)))
        (get mp nil)))))

(defn- -localize [v locale]
  (cond
    (:loc v) (localize-1 (:loc v) locale)
    (:locs v) (apply str (map #(-localize % locale) (:locs v)))
    :else
    (assert false (str "Not localizable:" (pr-str v)))))

(defn L
  "Creates a localizable value."
  [default & locales-strings]
  {:loc (merge {nil default}
               (apply hash-map locales-strings))})

(defn uni
  "Creates a localizable value, which is the same in all languages. Can be used for proper nouns or code."
  [s]
  (L s))

(defn >>
  "Creates a localizable string, as the concatenation of some other localizable strings."
  [a & args]
  {:locs (cons a args)})

(defn localize
  "Localize a localizable value for the given locale."
  [l locale]
  (-localize l locale))
