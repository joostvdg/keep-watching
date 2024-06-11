#FROM anapsix/alpine-java:8_jdk_unlimited
FROM eclipse-temurin:8-jre
ADD target/keep-watching-be.jar /app.jar
RUN bash -c 'touch /app.jar'
ENV JDBC_DATABASE_URL="jdbc:postgresql://localhost:5432"
ENV JDBC_DATABASE_USERNAME="docker"
ENV JDBC_DATABASE_PASSWORD="docker"
ENV PORT=8080
COPY docker/boot1/docker-run.sh /run.sh
RUN chmod +x /run.sh
CMD ["/run.sh"]
