(ns foe.authorization)

(declare ^{:dynamic true} *user*)

(defn- respond-403 []
  {:status 403
   :body   "Forbidden"})

(defn is-in-role?
  [role user]
  (some #(= role %) (:roles user)))

(defn wrap-authorize [handler]
  (fn [{:keys [user] :as request}]
    (binding [*user* user]
      (handler request))))

(defn require-authorization
  [role body]
  (if (is-in-role? role *user*)
          (body)
          (respond-403)))
