(ns foe.authentication
  (:require
    [ring.util.request :as req]
    [ring.util.response :as resp]
    [slingshot.slingshot :as slingshot]))

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
  "The session-auth-fn function will attempt to authenticate a request using the session. If the
   session contains a :user with :role and :name, authentication was successful."
  [request]
  (let [session (:session request)
        user    (:user session)]
    (if (and (contains? user :name)
             (contains? user :roles))
        user)))

(defn wrap-authentication
  "The wrap-authentication function attempts to authenticate the HTTP request using the supplied
   function auth-fn. If the authentication fails, a 401 will be returned, unless:
       - the request is whitelisted by the whitelist function, OR
       - allow-anonymous is true.

   When authentication is sccuessful, the request will include a new :user key, as supplied by the
   auth-fn function.

   The auth-fn function should return a map representing the authenticated user. When used with
   foe.authorization, this user map should include a :name and :roles key."
  [handler auth-fn & {:keys [allow-anonymous redirect-url whitelist]
                      :or   {allow-anonymous false
                             redirect-url nil
                             whitelist #{}}}]
  (fn [request]
    (if (whitelist (req/path-info request))
      (handler request))
      (slingshot/try+
        (let [user (auth-fn request)]
          (if user
            (handler (assoc request :user user))
            (redirect-or-401 handler request allow-anonymous redirect-url "Unauthorized")))
        (catch [:type :foe.exceptions/failed-auth] {:keys [message]}
          (redirect-or-401 handler request allow-anonymous redirect-url message)))))
