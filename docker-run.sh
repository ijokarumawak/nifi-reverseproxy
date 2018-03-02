#!/bin/bash

dirName=`dirname ${PWD}`
# n: nginx, haproxy ... etc
n=`basename ${dirName}`
# t: standalone-plain ... etc
t=`basename ${PWD}`

case $t in
  standalone-plain-http)
    p="-p 7070:7070"
    ;;
  standalone-plain-raw)
    p="-p 7080:7080 -p 7081:7081"
    ;;
  standalone-secure-http)
    p="-p 7443:7443"
    ;;
  standalone-secure-raw)
    p="-p 7444:7444 -p 7481:7481"
    ;;
  cluster-plain-http)
    p="-p 17080:17080"
    ;;
  cluster-plain-raw)
    p="-p 17090-17092:17090-17092"
    ;;
  cluster-secure-http)
    p="-p 17443:17443"
    ;;
  cluster-https-terminate)
    p="-p 17453:17453"
    ;;
  cluster-secure-raw)
    p="-p 17490-17492:17490-17492"
    ;;
esac

docker run -d --name ${n}_${t} ${p} --add-host nifi0:192.168.99.1 --add-host nifi1:192.168.99.1 --cap-add=NET_ADMIN ${n}:${t}


case $t in
  cluster-plain-http | cluster-secure-http | cluster-https-terminate)
    docker exec ${n}_${t} dnsmasq
    ;;
esac
