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
  [handler request allow-anonymous redirect-url whitelist]
  (if (or allow-anonymous
          (whitelist (req/path-info request)))
      (handler request)
      (if redirect-url
          (resp/redirect redirect-url)
          (respond-401 "Unauthorized"))))

(defn session-auth-fn
  [request]
  (let [session (:session request)
        user    (:user session)]
    (if (and (contains? user :name)
             (contains? user :roles))
        user)))

(defn wrap-authentication [handler auth-fn &
                           {:keys [allow-anonymous redirect-url whitelist]
                            :or {allow-anonymous false
                                 redirect-url nil
                                 whitelist #{}}}]
  (fn [request]
    (slingshot/try+
      (let [user (auth-fn request)]
        (if user
          (handler (assoc request :user user))
          (redirect-or-401 handler request allow-anonymous redirect-url whitelist)))
      (catch [:type :foe.exceptions/failed-auth] {:keys [message]}
        (respond-401 message)))))
