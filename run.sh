#/bin/bash

dirName=`dirname ${PWD}`
t=`basename ${dirName}`

case $t in
  standalone-plain)
    p=8080
    ;;
  standalone-secure)
    p=8443
    ;;
  cluster-plain)
    p=18080
    ;;
  cluster-secure)
    p=18443
    ;;
esac

docker run -d --name nifi-reverseproxy_${t} -p ${p}:${p} nifi-reverseproxy:${t}
