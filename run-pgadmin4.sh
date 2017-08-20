#!/usr/bin/env bash

docker run --name pgadmin4 \
           --link keepwatching_db-local_1 \
           -p 5050:5050 \
           --network keepwatching_default \
           -d fenglc/pgadmin4
