#!/usr/bin/env bash

echo "Spring Boot 1 Docker Run Script"

echo "Set ulimit to unlimited"
ulimit -c unlimited

echo "Set the JVM options"
echo "JDBC_DATABASE_URL=$JDBC_DATABASE_URL"
echo "JDBC_DATABASE_USERNAME=$JDBC_DATABASE_USERNAME"
echo "JDBC_DATABASE_PASSWORD=$JDBC_DATABASE_PASSWORD"
echo "PORT=$PORT"
echo "JAVA_OPTS=$JAVA_OPTS"

#java -Xms256M -Xmx480M \
#    -Djava.security.egd=file:/dev/./urandom \

java \
    -Dserver.port=${PORT} \
    -Ddb.url=${JDBC_DATABASE_URL} \
    -Dspring.datasource.url=${JDBC_DATABASE_URL} \
    -Ddb.password=${JDBC_DATABASE_PASSWORD} \
    -Dspring.datasource.password=${JDBC_DATABASE_PASSWORD} \
    -Ddb.user=${JDBC_DATABASE_USERNAME} \
    -Dspring.datasource.username=${JDBC_DATABASE_USERNAME} \
    $JAVA_OPTS -jar /app.jar \

