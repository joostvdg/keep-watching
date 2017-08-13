# Heroku

> Heroku is a cloud platform based on a managed container system, with integrated data services and a powerful ecosystem, for deploying and running modern apps. The Heroku developer experience is an app-centric approach for software delivery, integrated with today’s most popular developer tools and workflows.

Keep-Watching uses Heroku for its deployment/runtime environment.

Allowing free hosting of the docker container running the Spring Boot backend and a free postgresql database for the storage.
 
## Heroku configuration

In Heroku there isn't much to configure.

* create new app
* add postgresql database via configure add-ons

## Docker configuration

As Heroku will manage the postgresql database itself in a IAAS (EC2), the database isn't fixed.

So instead of a having a fixed URL and credentials, Heroku makes sure your runtime gets this information via environment variables.

How this works, can be read [here](https://devcenter.heroku.com/articles/connecting-to-relational-databases-on-heroku-with-java).

Unfortunately, this complicates the Docker configuration, as the runtime environment variable is url of the following format:

```bash
DATABASE_URL=postgres://user:password@heroku.com:5432/hellodb
```

Our complication come from the usage of JOOQ and Flyway, which need access to the Database prior to Spring loading.

So even if the format suits Spring configuration, the url needs to be parsed before the application can start.

That's why the Dockerfile looks like this:

```dockerfile
FROM anapsix/alpine-java:jdk8
ADD target/keep-watching-be.jar /app.jar
RUN bash -c 'touch /app.jar'
ENV JDBC_DATABASE_URL="jdbc:postgresql://localhost:5432"
ENV JDBC_DATABASE_USERNAME="docker"
ENV JDBC_DATABASE_PASSWORD="docker"
ENV PORT=8080
COPY docker-run.sh /run.sh
RUN chmod +x /run.sh
CMD ["/run.sh"]
```

The [docker-run.sh](https://github.com/joostvdg/keep-watching/blob/master/backend/docker-run.sh) will check for the Heroku environment variable (DATABASE_URL).

If it isn't there, the three JDBC variables can either be left to default or set via any other docker mechanism.

### Start command

Just for saving you the trouble to look up the docker-run.sh file, here is the start command. 
The Java environment variables are suited for Heroku deployment in the free tier (beyond 512mb Heroku will kill it).

```bash
java -Xms256M -Xmx512M -Djava.security.egd=file:/dev/./urandom -Dserver.port=$PORT -Ddb.url=$JDBC_DATABASE_URL -Dspring.datasource.url=$JDBC_DATABASE_URL -Ddb.password=$JDBC_DATABASE_PASSWORD -Dspring.datasource.password=$JDBC_DATABASE_PASSWORD -Ddb.user=$JDBC_DATABASE_USERNAME -Dspring.datasource.username=$JDBC_DATABASE_USERNAME -jar /app.jar
```

## How to deploy

There are various way's to deploy Spring Boot applications to Heroku.

Due to our reliance on JOOQ and Flyway it is a little complicated to create a build that is the same locally, in Circle CI and in Heroku.

So the current approach is to just push an existing Docker image to our personal Heroku registry.

```bash
docker login --email=${HEROKU_EMAIL} --username=${HEROKU_USER} --password=${HEROKU_TOKEN} registry.heroku.com
docker tag caladreas/keep-watching-be registry.heroku.com/keep-watching/web
docker push registry.heroku.com/keep-watching/web
```

## Resources

* [Spring Boot Docker deployment on Heroku](https://toedter.com/2016/11/05/deploying-spring-boot-apps-to-heroku-using-docker/)
* [Spring Boot on Heroku tutorial](https://devcenter.heroku.com/articles/deploying-spring-boot-apps-to-heroku)
* [Heroku API Authentication tutorial](https://devcenter.heroku.com/articles/platform-api-quickstart#authentication)


