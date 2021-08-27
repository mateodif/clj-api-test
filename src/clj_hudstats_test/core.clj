(ns clj-hudstats-test.core
  (:gen-class))

(require '[clojure.string :as str]
         '[reitit.ring :as ring]
         '[ring.adapter.jetty :as jetty]
         '[clj-http.client :as client]
         '[clojure.data.json :as json]
         '[reaver :as reaver])

(def base-api-url "https://apis.is/rides/")

(defn ping-handler [_]
  {:status 200 :body "pong"})

(defn request-and-parse-json [link]
  (json/read-str (:body (client/get link)) :key-fn keyword))

;; (defn map-by-key [key to-map]
;;   (map (fn [item] (get item key)) to-map))

;; (defn get-html-by-key [key api-map]
;;   (let [urls (map-by-key key api-map)
;;         responses (map (fn [url] (client/get url)) urls)]
;;     (map (fn [response] (:body response)) responses)))

(defn assoc-html-by-key [key api-map]
  (map (fn [hshmp]
         (assoc hshmp :html (:body (client/get (get hshmp key)))))
       api-map))

(defn parse-drivers-html [html-body]
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
   The following transformation is applied:
   The first value of a map as the key and the second value as the value.
   (transform-value-as-key [{:a 1 :b 2} {:c 3 :d 4}]) => [{:1 2} {:3 4}]"
  [map-seq]
  (reduce (fn [res hmap]
            (let [values (vals hmap)]
              (merge res {(format-as-keyword (first values))
                          (second values)})))
          []
          map-seq))

(defn extend-extra-info [hmap]
  (json/write-str (map (fn [temp]
                         (let [parsed-html (parse-drivers-html (:html temp))
                               extra-info (transform-value-as-key parsed-html)]
                           (merge (dissoc temp :html) (into {} extra-info))))
                       (assoc-html-by-key :link hmap))))

(defn drivers-handler [_]
  {:status 200 :body
   (extend-extra-info
    (:results (request-and-parse-json (str base-api-url "samferda-drivers"))))})

(def app
  (ring/ring-handler
   (ring/router
    ["/api"
     ["/ping" ping-handler]
     ["/drivers" drivers-handler]])
   (ring/create-default-handler)))

(defn -main
  "Initialization of the Jetty server"
  [& args]
  (jetty/run-jetty app {:port 3000}))
