#!/bin/bash

dirName=`dirname ${PWD}`
# n: nginx, haproxy ... etc
n=`basename ${dirName}`
# t: standalone-plain ... etc
t=`basename ${PWD}`
docker build --force-rm -t ${n}:${t} .
