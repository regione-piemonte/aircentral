#!/bin/bash

HOST=$1
STATION_NAME=$2
PERIFERICO_VERSION=$3
COMM_DEVICE=$4

CALLED_NAME=`readlink $0 || basename $0`
SCRIPT_NAME=`dirname $0`/$CALLED_NAME
USERTASK_DIR=`dirname $SCRIPT_NAME`
LOG_DIR=$USERTASK_DIR/log
if [ -z $STATION_NAME ]; then
  LOG_FILE=/dev/stdout
else
  LOG_FILE=$LOG_DIR/"${STATION_NAME// /_}".log
fi

mkdir -p $LOG_DIR
sshpass -p srqadmin ssh periferico@$HOST periferico/.auto-install/current-release/update-periferico.sh InstallNow >>$LOG_FILE 2>&1
