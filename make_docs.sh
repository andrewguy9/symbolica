#! /bin/bash

rm -rf target out docs
clojure -M:build-prod
mkdir docs
cp target/public/cljs-out/prod-main.js docs/
cp -r resources/public/ docs
perl -i -p -e 's/cljs-out\/dev-main.js/prod-main.js/' docs/index.html
