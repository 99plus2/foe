# Foe

A Clojure library designed for easy and flexible authentication and
authorization, especially in ring-based applications.

Consists of authentication, authorization, and a series of
auth-specific libraries, including oauth2.

## Usage

To install, add the following to your project's `:dependencies` key:

```clojure
[foe "0.3.0"]
```

## Example

Consider a [Clams](https://github.com/standardtreasury/clams) app where we want to authn/z a request.  We can do
that in one step using Foe, assuming that each request contains the
needed user information:

```clojure
;;; ...

(require '[foe.authentication :refer [wrap-authentication]])
(require '[clams.app :as app])

(defn- find-user-from-request [req]
  ...)

(defn- my-auth-function [req]
  (if-let [user (find-user-from-request req)]
    {:roles ["user"] :guid (:id user)}
    {:error "Unauthorized"}))

(defn wrap-auth [app]
  (wrap-authentication app my-auth-function))

(defn -main [& args]
  (app/start-server 'sample {:middleware [wrap-auth]}))

```

## Testing

To run the Foe tests, just:

    lein test

## License

Copyright © 2015 Standard Treasury

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
