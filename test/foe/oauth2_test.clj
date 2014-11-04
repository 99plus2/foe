(ns foe.oauth2-test
  (:require [clojure.test :refer :all]
            [foe.oauth2 :as oauth2]))

(deftest test-oauth2
  (testing "create-authorization-url"
    (let [config {:client-id    "foo"
                  :authorize-url "http://wut.com/oauth/authorize"
                  :scopes       "wut bar"
                  :state        "IAMSTATEIAMRANDOM"
                  :redirect-uri "http://example.com/login"}
          url (oauth2/create-authorization-url config)]
      (is (= "http://wut.com/oauth/authorize?client_id=foo&state=stateTBD&redirect_uri=http%3A%2F%2Fexample.com%2Flogin&scope=wut+bar&response_type=code"
             url))))

  (testing "process-authorization-response"
    (let [response (oauth2/process-authorization-response "https://client.example.com/cb?code=SplxlOBeZQQYbYS6WxSbIA&state=xyz")]
      (is (= (:code response) "SplxlOBeZQQYbYS6WxSbIA"))
      (is (= (:state response) "xyz")))))
