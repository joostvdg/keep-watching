#!/usr/bin/env bash
DB_IP=$(docker inspect --format '{{.NetworkSettings.Networks.keepwatching_default.IPAddress}}' keepwatching_db-local_1)
mvn clean spring-boot:run -P db -Dspring.profiles.active=compose -Ddb.url=jdbc:postgresql://${DB_IP}:5432/kw -Dspring.datasource.url=jdbc:postgresql://${DB_IP}:5432/kw -Dspring.datasource.username=docker -Dspring.datasource.password=docke
