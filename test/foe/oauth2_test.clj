(ns foe.oauth2-test
  (:require [clojure.test :refer :all]
            [foe.oauth2 :as oauth2]))

(deftest create-authorization-url-test
  (let [config {:client-id    "foo"
                :authorize-url "http://wut.com/oauth/authorize"
                :scopes       "wut bar"
                :state        "IAMSTATEIAMRANDOM"
                :redirect-uri "http://example.com/login"}
        url (oauth2/create-authorization-url config)]
    (is (= "http://wut.com/oauth/authorize?client_id=foo&state=stateTBD&redirect_uri=http%3A%2F%2Fexample.com%2Flogin&scope=wut+bar&response_type=code"
           url))))

(deftest process-authorization-response-test
  (let [response (oauth2/process-authorization-response "https://client.example.com/cb?code=SplxlOBeZQQYbYS6WxSbIA&state=xyz")]
    (is (= (:code response) "SplxlOBeZQQYbYS6WxSbIA"))
    (is (= (:state response) "xyz"))))

(deftest wrap-oauth2-test
  (let [handler    (fn [req] {:test 1})
        oauth-code "abc123"]
    (with-redefs [foe.oauth2/fetch-access-token
                  (fn [& args]
                    (is (= (last args) oauth-code))
                    "TOKEN")]
      (testing "unrelated request"
        (let [request {:uri "/some/path" :query-string "foo=bar"}]
          (is (= ((oauth2/wrap-oauth2 handler {}) request) {:test 1}))))
      (let [query-string (format "code=%s&foo=bar" oauth-code)
            request      {:uri "/oauth/authorized" :query-string query-string}]
        (testing "oauth request"
          (is (= ((oauth2/wrap-oauth2 handler {}) request)
                 {:status  302
                  :headers {"Location" "/"}
                  :body    ""
                  :cookies {"foe_bearer_token" {:value "TOKEN"}}
                  :session {:user {:name "TOKEN" :roles ["user"]}}})))
        (testing "oauth request params pre-processed"
          (let [query-params {"code" oauth-code "foo" "bar"}]
            (is (= ((oauth2/wrap-oauth2 handler {}) (assoc request :query-params query-params))
                   {:status  302
                    :headers {"Location" "/"}
                    :body    ""
                    :cookies {"foe_bearer_token" {:value "TOKEN"}}
                    :session {:user {:name "TOKEN" :roles ["user"]}}}))))))))
