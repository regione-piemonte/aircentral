#!/bin/sh

STATION_NAME=$1
STATION_IP=$2
COP_DATA_TO_IMPORT_DIR=/home/copadmin/centrale/data
SSH="ssh -o BatchMode=yes -o ConnectTimeout=30"
SCP="scp -o BatchMode=yes -o ConnectTimeout=30 -C"
mkdir -p $COP_DATA_TO_IMPORT_DIR

if test "$STATION_NAME" = "CC-SITAF" ; then
  # Copia dati ETL
  ETL_DATA_DIR=/var/csi/etl/data
  ETL_DATA_TRANSFER_DIR=$ETL_DATA_DIR/moving2cop
  ETL_DATA_FILES=etlSitaf_????????_??????.csv
  $SSH periferico@$STATION_IP "find $ETL_DATA_DIR -type f -name $ETL_DATA_FILES -mmin +1 -maxdepth 1 -exec mv {} $ETL_DATA_TRANSFER_DIR/ \;" && \
  $SCP periferico@$STATION_IP:$ETL_DATA_TRANSFER_DIR/$ETL_DATA_FILES $COP_DATA_TO_IMPORT_DIR/ && \
  $SSH periferico@$STATION_IP "rm $ETL_DATA_TRANSFER_DIR/$ETL_DATA_FILES"
  # Copia dati grimm
  GRIMM_DATA_DIR=/var/csi/grimm/out
  GRIMM_DATA_TRANSFER_DIR=$GRIMM_DATA_DIR/moving2cop
  GRIMM_DATA_FILES=GrimmSitaf_????????_??????.csv
  $SSH periferico@$STATION_IP "find $GRIMM_DATA_DIR -type f -name $GRIMM_DATA_FILES -mmin +1 -maxdepth 1 -exec mv {} $GRIMM_DATA_TRANSFER_DIR/ \;" && \
  $SCP -C periferico@$STATION_IP:$GRIMM_DATA_TRANSFER_DIR/$GRIMM_DATA_FILES $COP_DATA_TO_IMPORT_DIR/ && \
  $SSH periferico@$STATION_IP "rm $GRIMM_DATA_TRANSFER_DIR/$GRIMM_DATA_FILES"; 
fi
if test "$STATION_NAME" = "TO-Lingotto" ; then
  # Copia dati grimm
  GRIMM_DATA_DIR=/home/grimm/out
  GRIMM_DATA_TRANSFER_DIR=$GRIMM_DATA_DIR/moving2cop
  GRIMM_DATA_FILES=GrimmLingotto_????????_??????.csv
  $SSH grimm@$STATION_IP "find $GRIMM_DATA_DIR -type f -name $GRIMM_DATA_FILES -mmin +1 -maxdepth 1 -exec mv {} $GRIMM_DATA_TRANSFER_DIR/ \;" && \
  $SCP -C grimm@$STATION_IP:$GRIMM_DATA_TRANSFER_DIR/$GRIMM_DATA_FILES $COP_DATA_TO_IMPORT_DIR/ && \
  $SSH grimm@$STATION_IP "rm $GRIMM_DATA_TRANSFER_DIR/$GRIMM_DATA_FILES";
fi

echo "RETURN CODE 0"
