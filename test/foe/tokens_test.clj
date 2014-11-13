(ns foe.tokens-test
  (:require [clojure.test :refer :all]
            [foe.tokens :as tokens]))

(deftest get-bearer-token-works
  (let [request-map {:remote-addr "localhost",
                     :params {},
                     :route-params {},
                     :headers
                     {"authorization" "Bearer 63Z2BBZSG76MINTIAQE2T3YOPGUFJI7Y",
                      "host" "localhost"},
                     :server-port 80,
                     :form-params {},
                     :query-params {},
                     :uri "/exchange_token",
                     :server-name "localhost",
                     :query-string nil,
                     :scheme :http,
                     :request-method :post}]
    (is (= (tokens/get-bearer-token request-map)
           "63Z2BBZSG76MINTIAQE2T3YOPGUFJI7Y"))))
