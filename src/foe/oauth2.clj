(ns foe.oauth2
  (:require
    [clj-http.client :as client]
    [clojure.walk :as walk]
    [foe.util :refer [?assoc]]
    [ring.middleware.params :refer [params-request]]
    [ring.util.codec :as codec]
    [ring.util.response :as resp]))

(defn fetch-access-token
  [{:keys [token-url client-id client-secret redirect-uri]} code]
  (let [response (client/post
                   token-url {:as          :json
                              :form-params {:grant_type    "authorization_code"
                                            :client_id     client-id
                                            :client_secret client-secret
                                            :code          code
                                            :redirect_uri  redirect-uri}})]
    (:body response)))

(defn get-token
  [handler request config code]
  (let [response (fetch-access-token config code)
        token (:access_key response)
        user {:roles ["user"] :guid (:guid response)}
        session (assoc (:session request) :user user)]
    (-> (resp/redirect "/")
        (assoc :session session)
        (resp/set-cookie (:token-cookie config "foe-bearer-token")
                         token
                         {:path "/"}))))

(defn ensure-query-params
  [request]
  (if (contains? request :query-params)
    request
    (params-request request)))

(defn get-query-param
  [request k]
  (get-in (ensure-query-params request) [:query-params k]))

(defn wrap-oauth2
  [handler config]
  (fn [request]
    (let [req-path (:uri request)
          code     (get-query-param request "code")]
      ;; TODO - match this better - we need to match the
      ;;        path of the redirect-url and the request path
      ;; https://github.com/standardtreasury-internal/foe/issues/5
      (if (= req-path "/oauth/authorized")
        (get-token handler request config code)
        (handler request)))))

(defn create-authorization-url
  "Creates the url for an authorization request.
   See: http://tools.ietf.org/html/rfc6749#section-4.1.1"
  [{:keys [client-id authorize-url redirect-uri scope state]}]
  (let [query-params (-> {"client_id" client-id
                          "response_type" "code"}
                          (?assoc "state" state
                                  "redirect_uri" redirect-uri
                                  "scope" scope))]
    (str authorize-url
         "?"
         (client/generate-query-string query-params))))

(defn process-authorization-response
  "Parses the response URL that issues a code.
   See: http://tools.ietf.org/html/rfc6749#section-4.1.2"
  [url]
  (let [parsed-url (client/parse-url url)]
    (walk/keywordize-keys (codec/form-decode (:query-string parsed-url)))))
