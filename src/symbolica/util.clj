(ns symbolica.util)

(defmacro infix
  "Use this macro when you pine for the notation of your childhood"
  [infixed]
  (list (second infixed) (first infixed) (last infixed)))

(defmacro with-out-str-return
  "Evaluates exprs in a context in which *print-fn* is bound to .append
  on a fresh StringBuffer.  Returns the string created by any nested
  printing calls, and the result of the expressions."
  [& body]
  `(let [sb# (goog.string/StringBuffer.)]
     (binding [cljs.core/*print-newline* true
               cljs.core/*print-fn* (fn [x#] (.append sb# x#))]
       (list ~@body (cljs.core/str sb#)))))
