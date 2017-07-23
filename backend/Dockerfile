FROM anapsix/alpine-java:jdk8
ADD target/keep-watching-be*.jar app.jar
RUN bash -c 'touch /app.jar'
ENV DB_URL="localhost:5432"
ENV DB_USER="docker"
ENV DB_PASS="docker"
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Ddb.url=jdbc:postgresql://${DB_URL}","-Dspring.datasource.url=jdbc:postgresql://${DB_URL}","-Dspring.datasource.password=${DB_PASS}","-Dspring.datasource.username=${DB_USER}","/app.jar"]
