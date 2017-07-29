FROM anapsix/alpine-java:jdk8
ADD target/keep-watching-be*.jar app.jar
RUN bash -c 'touch app.jar'
ENV JDBC_DATABASE_URL="jdbc:postgresql://localhost:5432"
ENV JDBC_DATABASE_USERNAME="docker"
ENV JDBC_DATABASE_PASSWORD="docker"
ENV PORT=8080
COPY entrypoint.sh /
ENTRYPOINT ["/entrypoint.sh"]
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=$PORT","-Ddb.url=$JDBC_DATABASE_URL","-Dspring.datasource.url=$JDBC_DATABASE_URL","-Ddb.password=$JDBC_DATABASE_PASSWORD","-Dspring.datasource.password=$JDBC_DATABASE_PASSWORD","-Ddb.user=$JDBC_DATABASE_USERNAME","-Dspring.datasource.username=$JDBC_DATABASE_USERNAME","-jar","app.jar"]
