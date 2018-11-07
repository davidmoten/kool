#!/bin/bash
set -e
mvn site
cd ../davidmoten.github.io
git pull
mkdir -p kool 
cp -r ../kool/target/site/* kool/
git add .
git commit -am "update site reports"
git push
