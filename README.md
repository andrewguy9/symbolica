Based on ClojureScript [getting started](https://clojurescript.org/guides/quick-start#clojurescript-compile)

Dependency work derived from [Dependencies guide](https://clojurescript.org/reference/dependencies)

Figwheel integration [tutorial](https://figwheel.org/tutorial.html)

# Running

## dev repl

Explicit: `clojure --main figwheel.main -co dev.cljs.edn --compile --repl`

Shortcut: `clojure -A:build-dev`


## production build
`clojure --main figwheel.main -co dev.cljs.edn --optimizations advanced --compile`

## production server
Explicit: `clojure -m figwheel.main --serve`

Shortcut: `clojure -A:build-dev`

# Build docs:

`./make_docs.sh` Then commit the changes.
