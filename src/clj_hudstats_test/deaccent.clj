(ns clj-hudstats-test.deaccent)

(require '[clojure.string :as str])

(defn deaccent
  "Remove accents from string
   Inspired by: https://gist.github.com/maio/e5f85d69c3f6ca281ccd
   todo: deal with special nordic characters like æ"
  [string]
  (let [normalized (java.text.Normalizer/normalize string java.text.Normalizer$Form/NFD)]
    (-> normalized
        (str/replace #"\p{InCombiningDiacriticalMarks}+" "") ; replace latin for [a-z] version
        (str/replace #"\p{C}" "") ; removes zero-width characters
        )))