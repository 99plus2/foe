(ns foe.util
  "Utility functions")

(defn ?assoc
  "Like assoc, but only for non-nil values."
  [m & kv]
  (apply assoc m 
    (flatten 
      (filter second 
        (partition 2 kv)))))
