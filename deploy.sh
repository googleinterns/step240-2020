#!/bin/sh
set -e
# set the project on Google Cloud Shell.
gcloud config set project llvm-build-dashboard
# build app
mvn clean package
# deploy app
gcloud app deploy target/step240-0.1-SNAPSHOT.jar
