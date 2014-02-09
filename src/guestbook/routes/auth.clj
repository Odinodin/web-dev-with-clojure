(ns guestbook.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [guestbook.views.layout :as layout]
            [guestbook.models.db :as db]
            [noir.response :refer [redirect]]
            [noir.validation :refer [rule errors? has-value? on-error]]
            [noir.session :as session]
            [noir.util.crypt :as crypt]
            [hiccup.form :refer [form-to label text-field password-field submit-button]]))

(defn format-error [[error]]
  [:p.error error])


(defn control [field name text]
  (list (on-error name format-error)
        (label name text)
        (field name)
        [:br]))


(defn login-page []
  (layout/common
   (form-to [:post "/login"]
            (control text-field :id "Screen name")
            (control password-field :pass "Password")
            (submit-button "login"))))





(defn handle-login [id pass]

  (let [user (db/get-user id)]
    (rule (has-value? id)
          [:id "Screen name is required"])
    (rule (has-value? pass)
          [:pass "password is required"])
    (rule (and user (crypt/compare pass (:pass user)))
          [:pass "invalid password"])

    (if (errors? :id :pass)
      (login-page)
      (do
        (session/put! :user id)
        (redirect "/")))))

(defn registration-page []
  (layout/common
   (form-to [:post "/register"]
            (control text-field :id "Screen name")
            (control password-field :pass "password")
            (control password-field :pass1 "retype password")
            (submit-button "create-accound"))))


(defn handle-registration [id pass pass1]
  (rule (= pass pass1)
        [:pass "password was not retyped correctly!!"])
  (if (errors? :pass)
    (registration-page)
    (do
      (println "REGISTERING!")
      (db/add-user-record {:id id :pass (crypt/encrypt pass)})
      (println "user created " (db/get-user id))
      (redirect "/login"))))

(defroutes auth-routes
  (GET "/register" [_] (registration-page))
  (POST "/register" [id pass pass1]
        (handle-registration id pass pass1))
  (GET "/login" [] (login-page))
  (POST "/login" [id pass]
        (handle-login id pass))
  (GET "/logout" []
       (layout/common
        (form-to [:post "/logout"]
                 (submit-button "logout"))))
  (POST "/logout" []
        (session/clear!)
        (redirect "/")))


