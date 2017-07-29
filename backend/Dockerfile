FROM anapsix/alpine-java:jdk8
ADD target/keep-watching-be.jar /app.jar
RUN bash -c 'touch /app.jar'
ENV JDBC_DATABASE_URL="jdbc:postgresql://localhost:5432"
ENV JDBC_DATABASE_USERNAME="docker"
ENV JDBC_DATABASE_PASSWORD="docker"
ENV PORT=8080
#COPY entrypoint.sh /
#ENTRYPOINT ["/entrypoint.sh"]
COPY docker-run.sh /run.sh
RUN chmod +x /run.sh
CMD ["/run.sh"]
