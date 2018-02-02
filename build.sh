#/bin/bash

dirName=`dirname ${PWD}`
t=`basename ${dirName}`
docker build --force-rm -t nifi-reverseproxy:${t} .
