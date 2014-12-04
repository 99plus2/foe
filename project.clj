(defproject foe
  ;; The version string is ignored in favor of the git tag that starts
  ;; with 'v'.
  "0.2.0-tag"
  :description "Foe: Flexible authentication and authorization"
  :url "http://standardtreasury.com/foe"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.1"]
                 [clj-http "1.0.0"]]
  :repositories {"internal" {:url "s3p://standard-releases/releases/"
                             :username :env
                             :passphrase :env
                             :sign-releases false}}
  :plugins [[lein-ancient "0.5.5"]
            [s3-wagon-private "1.1.2"]
            [jonase/eastwood "0.1.4"]
            [lein-test-out "0.3.1"]
            [org.clojars.cvillecsteele/lein-git-version "1.0.2"]]
  :profiles {:dev {:dependencies [[compojure "1.1.8"]
                                  [ring-mock "0.1.5"]]}})
