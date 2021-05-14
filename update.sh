#!/usr/bin/env bash

git pull
./gradlew

docker-compose down
docker-compose up -d
