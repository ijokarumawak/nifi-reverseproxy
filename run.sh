#/bin/bash

dirName=`dirname ${PWD}`
# n: nginx, haproxy ... etc
n=`basename ${dirName}`
# t: standalone-plain ... etc
t=`basename ${PWD}`

case $t in
  standalone-plain)
    p=8080
    ;;
  standalone-secure|standalone-secure-tcp)
    p=8443
    ;;
  cluster-plain)
    p=18080
    ;;
  cluster-secure)
    p=18443
    ;;
esac

docker run -d --name ${n}_${t} -p ${p}:${p} ${n}:${t}
