Based on ClojureScript [getting started](https://clojurescript.org/guides/quick-start#clojurescript-compile)

Figwheel integration [tutorial](https://figwheel.org/tutorial.html)

# Running

## dev repl

Explicit: `clojure --main figwheel.main -co dev.cljs.edn --compile --repl`

Shortcut: `clojure -A:build-dev`


## production build
`clojure --main figwheel.main -co dev.cljs.edn --optimizations advanced --compile`

## production server
`clojure -m figwheel.main --serve`
