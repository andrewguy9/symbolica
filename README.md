Based on ClojureScript [getting started](https://clojurescript.org/guides/quick-start#clojurescript-compile)

# Running

## dev repl

`clj --main cljs.main -co build.edn --compile symbolica.core --repl`

## production build
`clj --main cljs.main -co build.edn --optimizations advanced --compile symbolica.core`

## production server
`clj -m cljs.main --serve`
