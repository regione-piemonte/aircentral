#!/bin/sh

HOST=$1
STATION_NAME=$2
PERIFERICO_VERSION=$3
COMM_DEVICE=$4
USERTASKS_DIR=`dirname $0`

cd $USERTASKS_DIR
echo HOST=$HOST STATION=$STATION_NAME VERSION=$PERIFERICO_VERSION DEVICE=$COMM_DEVICE >> customtask.log
#$USERTASKS_DIR/get_import_files.sh $STATION_NAME $HOST
