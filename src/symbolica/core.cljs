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

(s/def ::num (s/cat :tag #{:number}
                    :number string?))
(s/def ::var (s/cat :tag #{:variable}
                    :name string?))
(def varspec ::var)

(s/def ::sum_nums (s/cat :operator #{:add}
                         :left  (s/and ::num)
                         :right (s/and ::num)))
(defn simplify_sum_nums [conformed]
  [:number
    (str
      (+
       (-> conformed :left :number int)
       (-> conformed :right :number int)))])

(s/def ::expr_sum_nums (s/cat :tag #{:expr}
                              :sum (s/and ::sum_nums)))

(s/def ::product_order (s/cat :operator #{:mul}
                         :left  (s/and ::var)
                         :right (s/and ::num)))
(def product_order_spec ::product_order)

(defn simplify_product_order [conformed]
  [:mul
   [:number   (-> conformed :right :number)]
   [:variable (-> conformed :left :name)]])

(s/def ::product (s/cat :operator #{:mul}
                        :left  (s/and ::num)
                        :right (s/and ::var)))
(s/def ::product_sum (s/cat :operator #{:add}
                            :left (s/and ::product)
                            :right (s/and ::product)))
(def product_sum_spec ::product_sum)
(defn simplify_product_sum [conformed]
  [:mul
   [:number
    (str
      (+
       (-> conformed :left  :left :number int)
       (-> conformed :right :left :number int)))]
   [:variable (-> conformed :left :right :name)]])

(def simplifications
  [[::sum_nums simplify_sum_nums]
   [::product_order simplify_product_order]
   [::product_sum simplify_product_sum]])

(defn simplify_step [ast spec, reducer]
  ;(println "math_simplify" ast spec)
  (if (s/valid? spec ast)
    (let [conformed (s/conform spec ast)
          simplified (reducer conformed)]
      ;(println "" "valid" conformed)
      ;(println "" "now" simplified)
      simplified)
    ast))

(defn math_simplify [ast]
  (reduce (fn [acc [spec reducer]] (simplify_step acc spec reducer)) ast simplifications))

(defn math_eval [ast]
  (->> ast
     (insta/transform
       {:add +, :sub -, :mul *, :div /, :number cljs.tools.reader/read-string :expr identity})))

(defn math_render [ast]
  (let [tag (first ast)
        children (rest ast)
        ]
    (case tag
      :expr     (math_render (first children))
      :add      (str (math_render (first children)) "+" (math_render (second children)))
      :sub      (str (math_render (first children)) "-" (math_render (second children)))
      :mul      (str (math_render (first children)) "*" (math_render (second children)))
      :div      (str (math_render (first children)) "/" (math_render (second children)))
      :number   (first children)
      :variable (first children)
      ""
      )))

(defonce input (r/atom "1"))

(defn atom-input [value]
  [:pre [:input {:type "text"
                 :value @value
                 :style {:font-family "monospace"}
                 :on-change #(reset! value (-> % .-target .-value))}]])

(defn math-table []
  (fn []
    [:table {:border "1px solid black" }
     [:tr [:td "input"]    [:td [atom-input input]]]
     [:tr [:td "str"]       [:td [:pre @input]]]
     [:tr [:td "ast"]        [:td [:pre (with-out-str (pp/pprint (math @input)))]]]
     [:tr [:td "simplified"] [:td [:pre (with-out-str (pp/pprint (clojure.walk/postwalk math_simplify (math @input))))]]]
     [:tr [:td "rendered"]   [:td [:pre (math_render (clojure.walk/postwalk math_simplify (math @input)))]]]
     ]))

(let [text "2*x+3*x"; "x*5+2*x+1+2";"1+2+3" ; "1-2/(3-4)+5*6"
      ast        (math text)
      simplified (clojure.walk/postwalk math_simplify ast)
      ]
  (rd/render
    [math-table]
    (js/document.getElementById "parse-test")))


