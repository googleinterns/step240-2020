#!/bin/sh
gcloud config set project llvm-build-dashboard &&
mvn package com.google.cloud.tools:appengine-maven-plugin:deploy
