#!/usr/bin/env bash

NUM="01"
APP_NAME="engEstoque"
CONTAINER_NAME="eng_estoque"
WAR_FILE="build/libs/engEstoque-1.0-SNAPSHOT.war"

BRANCH=`git branch | grep "*" | awk '{print $2}'`
IP=`ip route get 8.8.8.8 | sed -n '/src/{s/.*src *\([^ ]*\).*/\1/p;q}'`

if [[ "$BRANCH" == "master" ]]
then
  PORT="8${NUM}1"
elif [[ "$BRANCH" == "develop" ]]
then
  PORT="8${NUM}2"
else
  exit
fi

echo "version: "3"
services:
    engecopi:
        image: tomcat:8
        container_name: $CONTAINER_NAME
        ports:
            - \"$PORT:8080\"
        environment:
            TZ: "America/Fortaleza"
            EBEAN_PROPS: /etc/ebean.properties
        volumes:
            - ./$WAR_FILE:/usr/local/tomcat/webapps/$APP_NAME.war
            - ./logs:/usr/local/tomcat/logs
            - ./ebean.properties:/etc/ebean.properties
        restart: always
        networks:
            rede_$CONTAINER_NAME:
                ipv4_address: 10.201.$NUM.1
networks:
    rede_$CONTAINER_NAME:
        ipam:
            driver: default
            config:
                - subnet: 10.201.$NUM.0/24
" > docker-compose.yml

git fetch --all
git reset --hard
git pull

gradle clean build

docker-compose down
docker-compose up -d

echo "Acesse o aplicativo atraves do endereco:"
echo "http://$IP:$PORT/$CONTAINER_NAME"