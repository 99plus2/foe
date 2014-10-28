(ns foe.oauth2
  (:require [clj-http.client :as client]
            [ring.util.codec :as codec]
            [clojure.walk :as walk]))

(defn create-authorization-url
  "Creates the url for an authorization request.
   See: http://tools.ietf.org/html/rfc6749#section-4.1.1"
  [client-id auth-uri scopes state redirect-uri]
  (str auth-uri
       "?"
       (client/generate-query-string {"client_id"     client-id
                                      "state"         state
                                      "redirect_uri"  redirect-uri
                                      "scope"         scopes
                                      "response_type" "code"})))

(defn process-authorization-response
  "Parses the response URL that issues a code.
   See: http://tools.ietf.org/html/rfc6749#section-4.1.2"
  [url]
  (let [parsed-url   (client/parse-url url)
        query-string (walk/keywordize-keys (codec/form-decode (:query-string parsed-url)))]
    query-string))
