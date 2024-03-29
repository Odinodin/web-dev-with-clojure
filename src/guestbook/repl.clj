(ns guestbook.repl
  (:use guestbook.handler
        ring.server.standalone

        [ring.middleware file-info file])
  ;; Standard stuff
  (:require [clojure.pprint :refer [pprint]]
            [guestbook.models.db :as db]
            [guestbook.routes.auth :as auth]
            [guestbook.routes.home :as home]))


(defonce server (atom nil))

(defn get-handler []
  ;; #'app expands to (var app) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (-> #'app
    ; Makes static assets in $PROJECT_DIR/resources/public/ available.
    (wrap-file "resources")
    ; Content-Type, Content-Length, and Last Modified headers for files in body
    (wrap-file-info)))

(defn start-server
  "used for starting the server in development mode from REPL"
  [& [port]]
  (let [port (if port (Integer/parseInt port) 8080)]
    (reset! server
            (serve (get-handler)
                   {:port port
                    :init init
                    :auto-reload? true
                    :destroy destroy
                    :join true}))
    (println (str "You can view the site at http://localhost:" port))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))

#_(start-server)
#_(stop-server)


#_(auth/handle-login "odin" "zak123")

#_(db/create-guestbook-table)


#_(clojure.pprint/pprint (db/read-guests))

#_(db/save-message "odin" "trala lla")

#_(db/clear)

#_(db/get-user "odinodin")

;; Users
#_(db/create-user-table)

#_(db/add-user-record {:id "odin" :pass "abc"})

#_(db/get-user "odin")

