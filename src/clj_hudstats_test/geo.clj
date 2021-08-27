(ns clj-hudstats-test.geo)

(require '[clj-http.client :as client]
         '[clj-hudstats-test.requests :refer [request-and-parse-json]])

(def nominatim-api-url "https://nominatim.openstreetmap.org/search.php?")
(def earth-radius 6371)

(defn haversine [a b]
  (Math/pow (Math/sin (/ (- b a) 2)) 2))

(defn distance [coord1 coord2]
  (let [lat1 (:lat coord1) lon1 (:lon coord1)
        lat2 (:lat coord2) lon2 (:lon coord2)]
    (int
     (* 2 earth-radius (Math/asin (Math/sqrt (+ (haversine lat1 lat2)
                                                (* (Math/cos lat1)
                                                   (Math/cos lat2)
                                                   (haversine lon1 lon2)))))))))

(defn get-coordinates-by-city [city]
  (let [from-url (str nominatim-api-url
                      (client/generate-query-string {"q" (str city ",Iceland")
                                                     "format" "jsonv2"}))
        coordinates-map (first (request-and-parse-json from-url))]
    {:lon (Float/parseFloat (:lon coordinates-map))
     :lat (Float/parseFloat (:lat coordinates-map))}))

(defn add-distance [hmap]
  (let [from-coords (get-coordinates-by-city (:from hmap))
        to-coords (get-coordinates-by-city (:to hmap))]
    (assoc hmap :distance (str (distance from-coords to-coords) "km"))))
