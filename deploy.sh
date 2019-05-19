#!/usr/bin/env bash

VERSAO=""

MSG_COMMIT="$1"

DIR="engEstoqueTeste"
ARQ_VERSAO="src/main/resources/versao.txt"

VERSAO_OLD=`cat ${ARQ_VERSAO}`
sshpass -pengecopi2017 ssh root@192.168.1.14 cp -vf /root/${DIR}/engEstoque.war /root/${DIR}/bak/engEstoque_${VERSAO_OLD}.war

if [[ "$VERSAO" = "" ]]
then
  V1=`cat ${ARQ_VERSAO} | cut -d"." -f1`
  V2=`cat ${ARQ_VERSAO} | cut -d"." -f2`
  V3=$(( "$V2 + 1" ))
  VERSAO="${V1}.${V3}"
fi

echo ${VERSAO} > ${ARQ_VERSAO}

git.sh "deploy $VERSAO: $MSG_COMMIT"

./gradlew

sshpass -pengecopi2006 rsync -av \
       build/libs/engEstoque-1.0-SNAPSHOT.war root@192.168.1.14:/root/${DIR}/engEstoque.war

#sshpass -pengecopi2017 scp build/libs/engEstoque-1.0-SNAPSHOT.war root@192.168.1.14:/root/engEstoqueTeste/engEstoque.war