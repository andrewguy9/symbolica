Based on ClojureScript [getting started](https://clojurescript.org/guides/quick-start#clojurescript-compile)

# Running

## dev repl

`clj --main cljs.main --compile symbolica.core --repl`

## production build
`clj -m cljs.main --optimizations advanced -c symbolica.core`

## production server
`clj -m cljs.main --serve`
