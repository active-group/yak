(ns yak.locale
  "Locales usable for yank internationalization and localization."
  (:require [yak.core :as core]
            [clojure.string :as string]))

(extend-type String
  core/Locale
  (-reduce [this]
    (let [p (string/split this #"_" 2)]
      (when (= 2 (count p))
        (first p))))
  (-to-string [this] this)
  (-from-string [this s] s))

(comment
  #?(:clj
     (extend-type java.util.Locale
       (-reduce [this]
         (if (= this Locale/ROOT))
         ))
     ))
