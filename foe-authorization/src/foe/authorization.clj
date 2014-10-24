(ns foe.authorization)

(declare ^{:dynamic true} *user*)

(defn- list-contains? [coll value]
  (let [s (seq coll)]
    (if s
      (if (= (first s) value) true (recur (rest s) value))
      false)))

(defn- respond-403 []
  {:status 403
   :body   "Forbidden"})

(defn is-in-role?
  [role user]
  (if (list-contains? (:roles user) role)
    true))

(defn wrap-authorize [handler]
  (fn [request]
    (let [user (:user request)]
      (binding [*user* user]
       (handler request)))))

(defn require-authorization
  [role body]
  (if (is-in-role? role *user*)
          (body)
          (respond-403)))
