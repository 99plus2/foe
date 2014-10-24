(ns foe.authentication-test
  (:require [clojure.test :refer :all]
            [ring.util.response :as resp]
            [ring.mock.request :as mock]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [foe.authentication :as authn]))

(defn- fake-auth
  [request]
  {:name "Keith" :roles ["user"]})

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/name" request (:name (:user request)))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (authn/wrap-auth fake-auth :allow-anonymous true)))

(deftest test-session
  (testing "Session-auth-fn returns user"
    (let [request {:session {:user {:name "Keith" :roles ["user"]}}}
          user    (authn/session-auth-fn request)]
      (is (= (:name user) "Keith")))))

(deftest test-app ;; test anonymous allowed by authenticate
  (testing "Redirect works"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 302))))

  (testing "404 works"
    (let [response (app (mock/request :get "/foo"))]
      (is (= (:status response) 404))))

  (testing "Return the username"
    (let [response (app (mock/request :get "/name"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Keith")))))
