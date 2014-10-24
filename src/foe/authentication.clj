(ns foe.authentication
  (:require [ring.util.response :as resp]))

(declare ^{:dynamic true} *user*)

(defn- respond-401 []
  {:status 401
   :body   "Unauthorized"})

(defn- redirect-or-401
  [handler request allow-anonymous redirect-url]
  (if allow-anonymous
    (handler request)
    (if redirect-url
      (resp/redirect redirect-url)
      (respond-401))))

(defn session-auth-fn
  [request]
  (let [session (:session request)
        user    (:user session)]
    (if (and (contains? user :name)
             (contains? user :roles))
        user)))

(defn wrap-auth [handler auth-fn & {:keys [allow-anonymous redirect-url]
                                    :or {allow-anonymous false redirect-url nil}}]
  (fn [request]
    (let [user (auth-fn request)]
      (if user
        (binding [*user* user]
          (handler (assoc request :user user)))
        (redirect-or-401 handler request allow-anonymous redirect-url)))))
