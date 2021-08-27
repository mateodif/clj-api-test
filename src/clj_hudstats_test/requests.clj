(ns clj-hudstats-test.requests 
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(defn request-and-parse-json [link]
  (json/read-str (:body (client/get link)) :key-fn keyword))

(defn assoc-html-by-key [key api-map]
  (map (fn [hshmp]
         (assoc hshmp :html (:body (client/get (get hshmp key)))))
       api-map))
