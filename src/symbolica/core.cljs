(ns symbolica.core
  (:require react-dom
            [goog.dom :as dom]
            [goog.dom.classes :as classes]
            [goog.events :as events])
  (:import [goog Timer]))

(println "Hello ClojureScript React!")

(.render js/ReactDOM
         (.createElement js/React "h2" nil "Hello, React!")
         (.getElementById js/document "app"))

(let [element (dom/createDom "div" "some-class" "Hello Google Closure")]
  (classes/enable element "another-class" true)
  (-> (dom/getDocument)
    .-body
    (dom/appendChild element)))
