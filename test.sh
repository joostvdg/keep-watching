#!/usr/bin/env bash
echo "##############"
echo "## MAVEN TEST"
echo "##############"
echo "## START DB"
docker-compose pull
docker-compose up -d db
sleep 5
docker logs keepwatching_db_1
echo "## CHECK DB IP"
DB_IP=$(docker inspect --format '{{.NetworkSettings.Networks.keepwatching_default.IPAddress}}' keepwatching_db_1)
echo "# IP=${DB_IP}"
echo "##############"
echo "##############"
echo "## RUN MAVEN TESTS"
cd backend
mvn flyway:migrate generate-resources generate-sources package -e -Ddb.url="jdbc:postgresql://${DB_IP}:5432/kw" -Drun.jvmArguments="-Dencrypt.keyStore.secret=${JKS_SECRET} -Dencrypt.keyStore.password=${JKS_PASS} -Dspring.datasource.url=jdbc:postgresql://${DB_IP}:5432/kw -Dspring.datasource.username=docker -Dspring.datasource.password=docker"
cd ..
echo "##############"
echo "##############"
echo "## STOP DB"
docker-compose down
echo "##############"
