(ns clj-hudstats-test.core
  (:gen-class))

(require '[clojure.string :as str]
         '[reitit.ring :as ring]
         '[ring.adapter.jetty :as jetty]
         '[clojure.data.json :as json]
         '[reaver :as reaver]
         '[clj-hudstats-test.geo :as geo]
         '[clj-hudstats-test.requests :refer [request-and-parse-json assoc-html-by-key]]
         '[clj-hudstats-test.deaccent :refer [deaccent]])

(def base-api-url "https://apis.is/rides/")
(def drivers-url (str base-api-url "samferda-drivers"))
(def passengers-url (str base-api-url "samferda-passengers"))

(defn ping-handler [_]
  {:status 200 :body "pong"})

(defn parse-samferda-html [html-body]
  (reaver/extract-from (reaver/parse html-body) "tr"
                       [:label :value]
                       "td > b" reaver/text
                       "td:last-child" reaver/text))

(defn format-as-keyword [string]
  (-> string
      (str/lower-case)
      (str/replace #":" "")
      (str/replace #" " "-")
      (keyword)))

(defn transform-value-as-key
  "Takes a sequence of two-valued maps.
   Transforms the first value of a map as the key and the second value as the value.
   Returns a map of all the key and values.
   (transform-value-as-key [{:a 1 :b 2} {:c 3 :d 4}]) => {:1 2 :3 4}"
  [map-seq]
  (into {}
        (reduce (fn [res hmap]
                  (let [values (vals hmap)]
                    (merge res {(format-as-keyword (first values))
                                (deaccent (second values))})))
                []
                map-seq)))


(defn extend-extra-info [hmap]
  (json/write-str
   (map (fn [trip]
          (let [parsed-html (parse-samferda-html (:html trip))
                extra-info (transform-value-as-key parsed-html)
                extra-info (geo/add-distance extra-info)]
            (merge (dissoc trip :html) extra-info)))
        (assoc-html-by-key :link hmap))))

(defn extend-api-url [url]
  (extend-extra-info
   (:results (request-and-parse-json url))))

(defn drivers-handler [_]
  {:status 200 :body (extend-api-url drivers-url)})

(defn passengers-handler [_]
  {:status 200 :body (extend-api-url passengers-url)})

(def app
  (ring/ring-handler
   (ring/router
    ["/api"
     ["/ping" ping-handler]
     ["/drivers" drivers-handler]
     ["/passengers" passengers-handler]])
   (ring/create-default-handler)))

(defn -main
  "Initialization of the Jetty server"
  [& args]
  (jetty/run-jetty app {:port 3000}))
