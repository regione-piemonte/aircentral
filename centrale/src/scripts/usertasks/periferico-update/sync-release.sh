#!/bin/bash

HOST=$1
STATION_NAME=$2
PERIFERICO_VERSION=$3
COMM_DEVICE=$4

TIMEOUT=180
CALLED_NAME=`readlink $0 || basename $0`
SCRIPT_NAME=`dirname $0`/$CALLED_NAME
USERTASK_DIR=`dirname $SCRIPT_NAME`
LOG_DIR=log
if [ -z $STATION_NAME ]; then
  LOG_FILE=/dev/stdout
else
  LOG_FILE=$LOG_DIR/"${STATION_NAME// /_}".log
fi

cd $USERTASK_DIR && \
mkdir -p $LOG_DIR && \
echo -e "\n"`date "+%Y%m%d %H%M%S"` "- Release Sync started" >> $LOG_FILE && \
sshpass -p srqadmin rsync -e ssh -avzr --checksum --partial --progress\
 --timeout=$TIMEOUT --delete current-release\
 periferico@$HOST:periferico/.auto-install/ >>$LOG_FILE 2>&1
