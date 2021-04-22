(ns symbolica.core
  (:require react-dom
            [instaparse.core :as insta]
            [clojure.pprint :as pp]
            [clojure.core]
            [clojure.walk]
            [reagent.core :as r]
            [reagent.dom :as rd]
            [clojure.spec.alpha :as s]
            ))

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


(s/def ::sum_order (s/cat :operator #{:add}
                         :left  (s/and ::num)
                         :right (s/and ::product)))
(defn simplify_sum_order [conformed]
  [:add
   [:mul [:number (-> conformed :right :left :number)] [:variable (-> conformed :right :right :name)]]
   [:number (-> conformed :left :number)]])

(def sum_order_spec ::sum_order)

(def simplifications
  [[::sum_nums simplify_sum_nums]
   [::product_order simplify_product_order]
   [::product_sum simplify_product_sum]
   [::sum_order simplify_sum_order]])

(defn simplify_step [ast spec, reducer]
  ;(println "math_simplify" ast spec)
  (if (s/valid? spec ast)
    (let [conformed (s/conform spec ast)
          simplified (reducer conformed)]
      (println "RULE" spec, "BEFORE" ast "AFTER" simplified)
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
    (let [ast (math @input)
          log (with-out-str (clojure.walk/postwalk math_simplify ast))
          simplified (clojure.walk/postwalk math_simplify ast)
          rendered (math_render simplified)]
      [:table {:border "1px solid black" }
       [:tr [:td "input"]      [:td [atom-input input]]]
       [:tr [:td "str"]        [:td [:pre @input]]]
       [:tr [:td "ast"]        [:td [:pre (with-out-str (pp/pprint ast))]]]
       [:tr [:td "log"]        [:td [:pre log]]]
       [:tr [:td "simplified"] [:td [:pre (with-out-str (pp/pprint simplified))]]]
       [:tr [:td "rendered"]   [:td [:pre rendered]]]
     ])))

(let [text "2*x+3*x"; "x*5+2*x+1+2";"1+2+3" ; "1-2/(3-4)+5*6"
      ast        (math text)
      simplified (clojure.walk/postwalk math_simplify ast)
      ]
  (rd/render
    [math-table]
    (js/document.getElementById "parse-test")))


