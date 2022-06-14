#!/bin/sh
cd current-release
find . -type f ! -name md5sum.txt -exec md5sum {} \; > md5sum.txt
