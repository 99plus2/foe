(ns foe.oauth2
  (:require [clj-http.client :as client]
            [ring.util.codec :as codec]
            [clojure.walk :as walk]
            [ring.util.response :as resp]
            [clj-http.client :as client]))

(defn fetch-access-token
  [token-url client-id client-secret redirect-uri code]
  (let [response (client/post
                   token-url {:as          :json
                              :form-params {:grant_type    "authorization_code"
                                            :client_id     client-id
                                            :client_secret client-secret
                                            :code          code
                                            :redirect_uri  redirect-uri}})]
    (:access_token (:body response))))

(defn get-token
  [handler request config code]
  (let [token (fetch-access-token (:token-url config)
                                  (:client-id config)
                                  (:client-secret config)
                                  (:redirect-uri config)
                                  code)
        ;; TODO - the token shouldn't be the name
        ;;        We need to use open id or include the
        ;;        name in the token response.
        user    {:name token :roles ["user"]}
        session (assoc (:session request) :user user)]
    (-> (resp/redirect "/")
        (assoc :session session))))

(defn wrap-oauth2 [handler config]
  (fn [request]
    (let [req-path (:uri request)
          code     (get (:query-params request) "code")]
      ;; TODO - match this better - we need to match the
      ;;        path of the redirect-url and the request path
      (if (= req-path "/oauth/authorized")
        (get-token handler request config code)
        (handler request)))))

(defn create-authorization-url
  "Creates the url for an authorization request.
   See: http://tools.ietf.org/html/rfc6749#section-4.1.1"
  [config]
  (let [client-id (:client-id config)
        authorize-url (:authorize-url config)
        scopes (:scopes config)
        state "stateTBD"
        redirect-uri (:redirect-uri config)]
    (str authorize-url
         "?"
         (client/generate-query-string {"client_id"     client-id
                                        "state"         state
                                        "redirect_uri"  redirect-uri
                                        "scope"         scopes
                                        "response_type" "code"}))))

(defn process-authorization-response
  "Parses the response URL that issues a code.
   See: http://tools.ietf.org/html/rfc6749#section-4.1.2"
  [url]
  (let [parsed-url   (client/parse-url url)
        query-string (walk/keywordize-keys (codec/form-decode (:query-string parsed-url)))]
    query-string))
