(ns clj-hudstats-test.core
  (:gen-class))

(require '[reitit.ring :as ring]
         '[ring.adapter.jetty :as jetty])

(defn ping-handler [_]
  {:status 200 :body "pong"})

(def app
  (ring/ring-handler
   (ring/router
    ["/api"
     ["/ping" ping-handler]])
   (ring/create-default-handler)))

(defn -main
  "Initialization of the Jetty server"
  [& args]
  (jetty/run-jetty app {:port 3000}))
