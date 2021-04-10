(ns symbolica.core
  (:require react-dom))

(println "Hello world!")

(.render js/ReactDOM
         (.createElement js/React "h2" nil "Hello, React!")
         (.getElementById js/document "app"))
