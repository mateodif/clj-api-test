(ns clj-hudstats-test.core
  (:gen-class))

(require '[reitit.ring :as ring]
         '[ring.adapter.jetty :as jetty]
         '[clj-http.client :as client]
         '[clojure.data.json :as json])

(def base-api-url "https://apis.is/rides/")

(defn ping-handler [_]
  {:status 200 :body "pong"})

(defn request-and-parse-json [url]
  (json/read-str (:body (client/get url)) :key-fn keyword))

(defn drivers-handler [_]
  {:status 200 :body
   (json/write-str
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
