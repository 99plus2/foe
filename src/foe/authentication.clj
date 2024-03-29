(ns foe.authentication
  (:require
    [ring.util.response :as resp]))

(defn- respond-401
  [message]
  {:status 401
   :body   message})

(defn- redirect-or-401
  [handler request allow-anonymous redirect-url message]
  (if allow-anonymous
      (handler request)
      (if redirect-url
          (resp/redirect redirect-url)
          (respond-401 message))))

(defn session-auth-fn
  "The session-auth-fn function will attempt to authenticate a request
   using the session. If the session contains a :user
   with :role and :guid, authentication was successful."
  [request]
  (let [user (get-in request [:session :user])]
    (if (and (contains? user :roles)
             (contains? user :guid))
    user
    {:error "Unauthorized"})))

(defn wrap-authentication
  "The wrap-authentication function attempts to authenticate the HTTP
   request using the supplied function auth-fn. If the authentication
   fails, a 401 will be returned, unless:

       - the request is whitelisted by the whitelist function, OR
       - allow-anonymous is true.

   The auth-fn function should return a map. If authentication is
   *NOT* successful, the map should contain a top-level `:error` key,
   with a string value indicating the reason for the failed
   authentication. The abscence of the `:error` key indicates
   successful authentication. On success, the request will include a
   new :user key, as supplied by the auth-fn function.

   Examples:

        - PASS: {:guid \"123-456\" :roles [\"user\"] :guid 1}
        - FAIL: {:error \"Error message as string\"}

   When used with foe.authorization, this user map should
   include :roles and :guid keys."
  [handler auth-fn & {:keys [allow-anonymous redirect-url whitelist]
                      :or   {allow-anonymous false
                             redirect-url nil
                             whitelist #{}}}]
  (fn [request]
    (if (whitelist request)
      (handler request)
      (let [{:keys [error guid roles] :as auth-map} (auth-fn request)]
        (if error
          (redirect-or-401 handler request allow-anonymous redirect-url error)
          (do
            ;; :guid and :roles are required for authentication to work properly
            (assert (every? identity [roles guid]))
            (handler (assoc request :user auth-map))))))))
