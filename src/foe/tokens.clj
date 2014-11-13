(ns foe.tokens)

(defn get-bearer-token
  "Extract a bearer token from a ring request map.

  Although there are multiple ways to pass a bearer token, this function only
  checks to see if it is present in the header, since that's the only
  recommended method and therefore the only one we care about."
  [request-map]
  (let [auth-header (-> request-map
                        :headers
                        (get "authorization"))]
    (if auth-header
      (let [[auth-type auth-val] (clojure.string/split auth-header #" ")]
        (if (= auth-type "Bearer") auth-val)))))
