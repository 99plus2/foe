(ns foe.oauth2
  (:require [clj-http.client :as client]))

(defn create-authorization-url
  [client-id auth-uri scopes state redirect-uri]
  (str auth-uri
       "?"
       (client/generate-query-string {"client_id"     client-id
                                      "state"         state
                                      "redirect_uri"  redirect-uri
                                      "scope"         scopes
                                      "response_type" "code"})))
