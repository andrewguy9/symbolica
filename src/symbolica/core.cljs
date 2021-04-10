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

(dom/replaceNode
  (dom/createTextNode "Hello from Google Closure")
  (dom/getElement "google-closure")
  )

(dom/replaceNode
  (dom/createTextNode (.getMessage (js/yayQuery)))
  (dom/getElement "external-js")
  )

(dom/replaceNode
  (dom/createTextNode (.getMessage (yq/yayQuery)))
  (dom/getElement "library-closure")
  )

(dom/replaceNode
  (dom/createTextNode (str "1+2 is " (js/add 1 2)))
  (dom/getElement "js-library")
  )

