(ns foe.authorization
  (:require [foe.authentication :as authn]))

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

(defn wrap-authorize [handler role]
  (fn [request]
    (let [user (:user request)]
      (if (is-in-role? role authn/*user*)
        (handler request)
        (respond-403)))))

(defn require-authorization
  [role body]
  (if (is-in-role? role authn/*user*)
          (body)
          (respond-403)))
