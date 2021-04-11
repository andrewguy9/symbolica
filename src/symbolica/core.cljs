(ns symbolica.core
  (:require react-dom
            [goog.dom :as dom]
            [goog.dom.classes :as classes]
            [goog.events :as events]
            [yq]
            [calc]
            )
  (:import [goog Timer]))

(.render js/ReactDOM
         (.createElement js/React "span" nil "Hello from Clojure React!")
         (.getElementById js/document "clojure-react"))

(defn replace_div [id, inner]
  (dom/replaceNode
    (dom/createDom "div" (clj->js {"id" id}) inner)
    (dom/getElement id)))

(replace_div "google-closure" "google clojure says hi")

(replace_div "external-js" (.getMessage (js/yayQuery)))

(replace_div "library-closure" (.getMessage (yq/yayQuery)))

(replace_div "js-library" (str "1+2 == " (js/add 1 2)))

