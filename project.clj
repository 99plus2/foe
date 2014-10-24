(defproject foe "0.0.3"
  :description "Foe: Flexible authentication and authorization"
  :url "http://standardtreasury.com/foe"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies
    [[org.clojure/clojure "1.6.0"]
     [ring "1.3.1"]
     [foe/foe-authentication "0.0.3"]
     [foe/foe-authorization "0.0.3"
      foe/foe-oauth2 "0.0.3"]]
  :plugins
    [[lein-sub "0.2.4"]]
  :sub
    ["foe-authentication"
     "foe-authorization"
     "foe-oauth2"])
