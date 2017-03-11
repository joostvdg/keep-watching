#!/usr/bin/env bash
echo "##############"
echo "## MAVEN PACKAGE"
echo "##############"
echo "## START DB"
docker-compose pull
docker-compose up -d db
sleep 15
docker logs keepwatching_db_1
docker-compose start db
sleep 5
docker logs keepwatching_db_1
echo "## CHECK DB IP"
DB_IP=$(docker inspect --format '{{.NetworkSettings.Networks.keepwatching_default.IPAddress}}' keepwatching_db_1)
echo "# IP=${DB_IP}"
echo "##############"
echo "##############"
echo "## RUN MAVEN PACKAGE"
mvn clean package -P db -Dspring.profiles.active=compose -Dspring.datasource.url=jdbc:postgresql://${DB_IP}:5432/kw -Dspring.datasource.username=docker -Dspring.datasource.password=docker
echo "##############"
echo "##############"
echo "## STOP DB"
docker-compose down
echo "##############"