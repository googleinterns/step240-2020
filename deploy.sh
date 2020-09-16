#!/bin/sh
set -e
# set the project
gcloud config set project llvm-build-dashboard
# build app
mvn clean package
# deploy app
gcloud app deploy target/step240-2020-1.0.jar
