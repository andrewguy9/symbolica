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
            [clojure.walk]
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

; (s/def ::num (s/cat :tag #{:number} :number (s/and string?))
(s/def ::num (s/cat :tag #{:number}
                    :number string?))
(s/def ::sum_nums (s/cat :operator #{:add}
                         :left (s/and ::num)
                         :right (s/and ::num)))
(s/def ::expr_sum_nums (s/cat :tag #{:expr}
                              :sum (s/and ::sum_nums)))
(defn simplify_expr_sum_nums [conformed]
  [:number
    (str
      (+
       (-> conformed :left :number int)
       (-> conformed :right :number int)))])

(defn math_simplify [ast]
  (println "math_simplify" ast)
  (if (s/valid? ::sum_nums ast)
    (let [conformed (s/conform ::sum_nums ast)
          simplified (simplify_expr_sum_nums conformed)]
      (println "" "valid" conformed)
      (println "" "now" simplified)
      simplified)
    ast))

(defn math_eval [ast]
  (->> ast
     (insta/transform
       {:add +, :sub -, :mul *, :div /, :number cljs.tools.reader/read-string :expr identity})))

(let [text "1+2+3" ; "1-2/(3-4)+5*6"
      ast        (math text)
      valid      (s/valid? ::expr_sum_nums ast)
      conformed  (s/conform ::expr_sum_nums ast)
      explained  (s/explain ::expr_sum_nums ast)
      simplified (clojure.walk/postwalk math_simplify ast)
      ]
  (rd/render [:table {:border "1px solid black" }
              [:tr [:td "text"]       [:td [:pre text]]]
              [:tr [:td "ast"]        [:td (with-out-str (pp/pprint ast))]]
              [:tr [:td "valid"]      [:td (with-out-str (pp/pprint valid))]]
              [:tr [:td "conformed"]  [:td (with-out-str (pp/pprint conformed))]]
              [:tr [:td "explain"]    [:td (with-out-str (pp/pprint explained))]]
              [:tr [:td "simplified"] [:td (with-out-str (pp/pprint simplified))]]
              ]
             (js/document.getElementById "parse-test")))
