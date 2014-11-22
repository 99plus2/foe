(ns foe.authentication-test
  (:require [clojure.test :refer :all]
            [ring.util.response :as resp]
            [ring.mock.request :as mock]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [foe.authentication :as authn]
            [slingshot.slingshot :refer [throw+]]))

(defn- fake-auth
  [request]
  {:name "Keith" :roles ["user"]})

(defn- auth-with-exceptions
  [request]
  (throw+ {:type :foe.exceptions/failed-auth :message "Some valid reason"}))

(defroutes test-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/version" [] "v1")
  (GET "/name" request (:name (:user request)))
  (route/not-found "Not Found"))

(def test-app
  (-> test-routes
      (authn/wrap-authentication fake-auth
                                 :allow-anonymous true
                                 :whitelist #{"/version"})))

(def test-app-with-exceptions
  (-> test-routes
      (authn/wrap-authentication auth-with-exceptions)))

(deftest test-wrap-authentication
  (testing "Redirect works"
    (let [response (test-app (mock/request :get "/"))]
      (is (= (:status response) 302))))

  (testing "404 works"
    (let [response (test-app (mock/request :get "/foo"))]
      (is (= (:status response) 404))))

  (testing "Whitelist works"
    (let [response (test-app (mock/request :get "/version"))]
      (is (= 200 (:status response)))
      (is (= "v1" (:body response)))))

  (testing "Return the username"
    (let [response (test-app (mock/request :get "/name"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Keith"))))

  (testing "Thrown exceptions are caught and 401'd"
    (let [response (test-app-with-exceptions (mock/request :get "/name"))]
      (is (= (:status response) 401))
      (is (= (:body response) "Some valid reason")))))

(deftest test-session-auth-fn
  (testing "Session-auth-fn returns user"
    (let [request {:session {:user {:name "Keith" :roles ["user"]}}}
          user    (authn/session-auth-fn request)]
      (is (= (:name user) "Keith")))))
