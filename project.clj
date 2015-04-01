(defproject foe
  "0.3.0"
  :description "Foe: Flexible authentication and authorization"
  :url "http://standardtreasury.com/foe"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.1"]
                 [clj-http "1.0.0"]]
  :plugins [[jonase/eastwood "0.1.4"]
            [lein-test-out "0.3.1"]]
  :profiles {:dev {:dependencies [[compojure "1.1.8"]
                                  [ring-mock "0.1.5"]]}})
