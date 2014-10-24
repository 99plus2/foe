(ns foe.authorization-test
  (:require [clojure.test :refer :all]
            [ring.util.response :as resp]
            [ring.mock.request :as mock]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [foe.authentication :as authn]
            [foe.authorization :as authz]))

(defn- fake-auth
  [request]
  (let [uri (:uri request)]
    (condp = uri
      "/works"  {:name "Keith" :roles ["user"]}
      "/noworks" {:name "Keith" :roles ["not a user"]}
      nil)))

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/works" request
       (if (authz/is-in-role? "user" (:user request))
          "it works!"
          {:status 403 :body "Forbidden"}))
  (GET "/noworks" []
        (if (authz/is-in-role? "user" authn/*user*)
          "it works!"
          {:status 403 :body "Forbidden"}))
  (GET "/notauthorized" []
     (authz/require-authorization "user" "it works!")))

(def app
  (-> app-routes
      (authn/wrap-auth fake-auth :allow-anonymous true)))

(deftest test-app
  (testing "Return 200"
    (let [response (app (mock/request :get "/works"))]
      (is (= (:status response) 200))))

  (testing "Return 403"
    (let [response (app (mock/request :get "/noworks"))]
      (is (= (:status response) 403))))

  (testing "Return 403 part 2"
    (let [response (app (mock/request :get "/notauthorized"))]
      (is (= (:status response) 403)))))
