(defproject foe/foe-oauth2 "0.0.4"
  :description "foe-auth2: Flexible OAuth2-based authentication built on foe-authentication."
  :url "http://standardtreasury.com/foe"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.1"]
                 [clj-http "1.0.0"]]
  :profiles
  {:dev {:dependencies [[compojure "1.1.8"]
                        [ring-mock "0.1.5"]
                        [foe/foe-authentication "0.0.4"]]}})
