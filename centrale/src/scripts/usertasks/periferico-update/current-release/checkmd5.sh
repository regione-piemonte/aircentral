#!/bin/bash
md5sum -c md5sum.txt
RESULT=$?
if [ $RESULT -eq 0 ]; then
  echo -e "\nOK\n"
else
  echo -e "\nERROR\n"
fi
exit $RESULT
