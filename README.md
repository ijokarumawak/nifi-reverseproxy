
# Apache NiFi ReverseProxy

This is an experiment project to improve Apache NiFi S2S so that it can work with Reverse Proxies such as Nginx or HA proxy in between S2S clients and remote NiFi cluster nodes.

Includes following resources:

- Different NiFi setups:
  - Standard Plain, Standard Secure, Cluster Plain, Cluster Secure
  - These NiFi instances are running directly on a localhost, using symbolic links to the local nifi SNAPSHOT buid directory to reduce disk usage
- Nginx docker files for various configuration evaluation
  - Configurations in this project assumes:
    - a docker host is running on a Virtual Box instance whose IP is 192.168.99.100
    - localhost has a network interface whose IP is 192.168.99.1 which can be accessed by the docker host and containers
- proxyclient: A java project to test S2S against different NiFi environment with or without a Reverse Proxy in the middle

## NiFi updates

This branch has improvements on NiFi side.
https://github.com/ijokarumawak/nifi/tree/s2s-reverse-proxy

## PORTS

In order to run different setup at the same time, following ports are used to avoid conflicts.

|Name|host|sp|ss|cp|cs|
|----|----|--|--|--|--|
|Nginx|Docker Host|7080,7081|7443,7481|17080,17060,18070,18071|18443,18460,18470,18471|
|nifi debug|localhost|8000|8001|8002,8003|8004,8005|
|nifi.web.http.port|localhost|8080|8443|18080,18090|18443,18444|
|nifi.remote.input.socket.port|localhost|8081|8481|18081,18091|18481,18491|
|nifi.cluster.node.protocol.port|localhost|N/A|N/A|18082,18092|18482,18492|
|Embed Zookeeper client port|localhost|N/A|N/A|2181|2182|
|Embed Zookeeper server|localhost|N/A|N/A|2888:3888|2889:3889|
|CacheServer|localhost|8010|8011|8012|8013|
|HandleHttpRequest|localhost|8020|8021|8022|8023|
|ListenHttp|localhost|8030|8031|8032,8033|8034,8035|
