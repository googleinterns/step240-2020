#!/bin/sh
gcloud config set project llvm-build-dashboard &&
mvn clean package &&
gcloud app deploy target/step240-2020-1.0.jar
