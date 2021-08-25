(defproject clj-hudstats-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [metosin/reitit-ring "0.5.15"]
                 [clj-http "3.12.3"]
                 ; [http.async.client "1.3.1"]
                 [org.clojure/data.json "2.4.0"]
                 [reaver "0.1.3"]]
  :main ^:skip-aot clj-hudstats-test.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
