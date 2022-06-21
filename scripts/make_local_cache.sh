#!/usr/bin/env sh

docker rm -f $(docker ps -a | grep "foodduck_cache")

docker pull redis

docker network create foodduck_network 2> /dev/null

docker run --name foodduck_cache -itd -p 6379:6379 -- network foodduck_network --restart unless-stopped redis

