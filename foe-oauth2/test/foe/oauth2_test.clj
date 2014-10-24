(ns foe.oauth2-test
  (:require [clojure.test :refer :all]
            [foe.oauth2 :as oauth2]))

(deftest test-oauth2
  (testing "create-authorization-url"
    (let [client-id    "foo"
          auth-uri     "http://wut.com/oauth/authorize"
          scopes       "wut bar"
          state        "IAMSTATEIAMRANDOM"
          redirect-uri "http://example.com/login"
          url (oauth2/create-authorization-url
                client-id auth-uri scopes state redirect-uri)]
      (is (= url "http://wut.com/oauth/authorize?client_id=foo&state=IAMSTATEIAMRANDOM&redirect_uri=http%3A%2F%2Fexample.com%2Flogin&scope=wut+bar&response_type=code")))))
