#!/usr/bin/env bash

git pull
./gradlew clean build -Pvaadin.productionMode

docker-compose down
docker-compose up -d
date

