#!/usr/bin/env sh

docker build -t foodduck .
docker rm -f foodduck_server
docker network create foodduck_network 2> /dev/null
docker run -itd --name foodduck_server -p 8080:8080 --network foodduck_network --restart unless-stopped foodduck
