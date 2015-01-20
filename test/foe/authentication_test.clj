(ns foe.authentication-test
  (:require [clojure.test :refer :all]
            [ring.util.response :as resp]
            [ring.mock.request :as mock]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [foe.authentication :as authn]))

(defn- fake-auth
  [request]
  {:name "Keith" :roles ["user"] :guid 1})

(defn- auth-that-fails
  [request]
  {:error "Some valid reason"})

(defn- auth-without-role
  [request]
  {:name "Mike" :guid 2})

(defroutes test-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/version" [] "v1")
  (GET "/name" request (:name (:user request)))
  (route/not-found "Not Found"))

(defn- stub-whitelist
  [request]
  (= (:path request) "/version"))

(def test-app
  (-> test-routes
      (authn/wrap-authentication fake-auth
                                 :allow-anonymous true
                                 :whitelist stub-whitelist)))

(def test-app-with-errors
  (-> test-routes
      (authn/wrap-authentication auth-that-fails)))

(def test-app-without-role
  (-> test-routes
      (authn/wrap-authentication auth-without-role)))

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

  (testing "Errors are caught and 401'd"
    (let [response (test-app-with-errors (mock/request :get "/name"))]
      (is (= (:status response) 401))
      (is (= (:body response) "Some valid reason"))))

  (testing "Auth without :roles 500's"
    (is (thrown? AssertionError (test-app-without-role (mock/request :get "/name"))))))

(deftest test-session-auth-fn
  (testing "Session-auth-fn returns user"
    (let [request {:session {:user {:name "Keith" :roles ["user"] :guid 1}}}
          user    (authn/session-auth-fn request)]
      (is (= (:name user) "Keith"))))

  (testing "Session-auth-fn returns error on failure"
    (let [request {:session {}}
          response (authn/session-auth-fn request)]
      (is (= (:error response) "Unauthorized")))))
