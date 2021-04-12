(ns symbolica.core
  (:require react-dom
            [goog.dom :as dom]
            [goog.dom.classes :as classes]
            [goog.events :as events]
            [yq]
            [calc]
            [instaparse.core :as insta]
            [clojure.pprint :as pp]
            [clojure.core]
            [reagent.core :as r]
            [reagent.dom :as rd]
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

(def math
  (insta/parser "
                expr = add-sub
                <add-sub> = mul-div | add | sub
                add = add-sub <'+'> mul-div
                sub = add-sub <'-'> mul-div
                <mul-div> = term | mul | div
                mul = mul-div <'*'> term
                div = mul-div <'/'> term
                <term> = number | <'('> add-sub <')'>
                number = #'[0-9]+'
                "))

(defn math_eval [ast]
  (->> ast
     (insta/transform
       {:add +, :sub -, :mul *, :div /, :number cljs.tools.reader/read-string :expr identity})))

(let [text "1-2/(3-4)+5*6"
      ast (math text)
      result (math_eval ast)]
  (rd/render [:div
              [:p text]
              [:p (with-out-str (pp/pprint ast))]
              [:p result]
              ]
             (js/document.getElementById "parse-test")))
