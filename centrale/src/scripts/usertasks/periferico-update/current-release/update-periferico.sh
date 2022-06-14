#!/bin/bash

START_KEY=$1
EXPECTED_KEY="InstallNow"
PERIFERICO_HOME=/var/csi/periferico
INSTALL_HOME=$PERIFERICO_HOME/.auto-install
BCK_HOME=$INSTALL_HOME/backup
BCK_DIR=`date +$BCK_HOME/%Y%m%d_%H%M`
MAX_BACKUPS=10

echo -e "\n"`date "+%Y%m%d %H%M%S"` "- Periferico Updater started"
if [ "$START_KEY" != "$EXPECTED_KEY" ]; then
  echo "Start key mismatch"
  exit 1
fi
diff $INSTALL_HOME/current-release/md5sum.txt $INSTALL_HOME/installed_version.txt > /dev/null
if [ $? -eq 0 ]; then
  echo "Already updated to latest version"
  exit 0
fi
cd $INSTALL_HOME/current-release && \
echo "Checking release integrity...." && \
./checkmd5.sh &&
cd $PERIFERICO_HOME && \
echo "Stopping Periferico..." && \
sudo /etc/init.d/periferico stop && \
echo "Backup in progress..." && \
mkdir -p $BCK_DIR && \
mv bin doc run.sh $BCK_DIR/ && \
mkdir -p $BCK_DIR/config && \
mv config/drivers $BCK_DIR/config/ && \
echo "Installation in progress..." && \
cd .. && \
cp -a $INSTALL_HOME/current-release/periferico . && \
find $INSTALL_HOME/current-release/ -name driver_*_bin_V*_????????.tgz -exec tar -xzf {} \; && \
cp $INSTALL_HOME/current-release/md5sum.txt $INSTALL_HOME/installed_version.txt && \
echo "Starting Periferico..." && \
sudo /etc/init.d/periferico start && \
echo  "Deleting old backups..." && \
find $BCK_HOME -mindepth 1 -maxdepth 1 -type d -name "????????_????" | \
sort | head -n -$MAX_BACKUPS | xargs --delimiter="\n" rm -rf && \
echo "Installation OK"
