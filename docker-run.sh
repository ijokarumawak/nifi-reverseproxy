#!/bin/bash

dirName=`dirname ${PWD}`
# n: nginx, haproxy ... etc
n=`basename ${dirName}`
# t: standalone-plain ... etc
t=`basename ${PWD}`

case $t in
  standalone-plain-http)
    p="-p 8080:8080"
    ;;
  standalone-plain-raw)
    p="-p 8080:8080 -p 8081:8081"
    ;;
  standalone-secure-http)
    p=8443
    ;;
  cluster-plain)
    p=18080
    ;;
  cluster-secure)
    p=18443
    ;;
esac

docker run -d --name ${n}_${t} ${p} ${n}:${t}
