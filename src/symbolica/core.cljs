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
            [clojure.spec.alpha :as s]
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
                <term> = number | variable | <'('> add-sub <')'>
                number = #'[0-9]+'
                variable = #'[a-zA-Z][a-zA-Z0-9]*'
                "))

(s/def ::num (s/tuple #{:number} string?))
(s/def ::sum_nums (s/tuple #{:add} ::num ::num))
(s/def ::expr_sum_nums (s/tuple #{:expr} ::sum_nums))

(defn math_simplify [ast]
  (with-out-str (s/conform ::expr_sum_nums ast))
  )

(defn math_eval [ast]
  (->> ast
     (insta/transform
       {:add +, :sub -, :mul *, :div /, :number cljs.tools.reader/read-string :expr identity})))

(let [text "1+2" ; "1-2/(3-4)+5*6"
      ast       (math text)
      valid     (s/valid? ::expr_sum_nums ast)
      conformed (s/conform ::expr_sum_nums ast)
      explained (s/explain ::expr_sum_nums ast)
      ]
  (rd/render [:table {:border "1px solid black" }
              [:tr [:td "text"]      [:td [:pre text]]]
              [:tr [:td "ast"]       [:td (with-out-str (pp/pprint ast))]]
              [:tr [:td "valid"]     [:td (with-out-str (pp/pprint valid))]]
              [:tr [:td "conformed"] [:td (with-out-str (pp/pprint conformed))]]
              [:tr [:td "explain"]   [:td (with-out-str (pp/pprint explained))]]
              ]
             (js/document.getElementById "parse-test")))
