# Upgrade to Spring Boot 2

## Requirements

* [SDKMan](https://sdkman.io/)
* [NVM](https://github.com/nvm-sh/nvm)
* NodeJs 6
* Docker
* Docker Compose
* Java (8)

## Get It To Work

Step one of any migration, is to establish a base line.

That means we need to get the application to run.

### Run Test.sh

We have a prepared script that will run the application and the tests.

```bash
./test.sh
```

Which contains:

```bash
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
mvn flyway:migrate generate-resources generate-sources package -e -Ddb.url="jdbc:postgresql://${DB_IP}:5432/kw" -Drun.jvmArguments="-Dencrypt.keyStore.secret=${JKS_SECRET} -Dencrypt.keyStore.password=${JKS_PASS} -Dspring.datasource.url=jdbc:postgresql://${DB_IP}:5432/kw -Dspring.datasource.username=docker -Dspring.datasource.password=docker" -Dencrypt.keyStore.secret=${JKS_SECRET} -Dencrypt.keyStore.password=${JKS_PASS} -Dspring.datasource.url=jdbc:postgresql://${DB_IP}:5432/kw -Dspring.datasource.username=docker -Dspring.datasource.password=docker
cd ..
echo "##############"
echo "##############"
echo "## STOP DB"
docker-compose down
echo "##############"
```

We get the following errors:

```shell
org.apache.maven.project.ProjectBuildingException: Some problems were encountered while processing the POMs:
[ERROR] Non-resolvable import POM: The following artifacts could not be resolved: org.springframework.cloud:spring-cloud-dependencies:pom:Ilford.BUILD-SNAPSHOT (absent): Could not find artifact org.springframework.cloud:spring-cloud-dependencies:pom:Ilford.BUILD-SNAPSHOT in spring-snapshots (https://repo.spring.io/snapshot) @ line 372, column 16
[ERROR] 'dependencies.dependency.version' for org.springframework.cloud:spring-cloud-starter-config:jar is missing. @ line 258, column 21
[ERROR] 'dependencies.dependency.version' for org.springframework.security:spring-security-rsa:jar is missing. @ line 262, column 21
```

### Fix Dependencies

We need to fix the dependencies in the `pom.xml` file.

Disabled the snapshots:

```xml
<repository>
    <id>spring-snapshots</id>
    <name>Spring Snapshots</name>
    <url>https://repo.spring.io/snapshot</url>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
</repository>
```

Looks like we rely on Ilford release, but the oldest available is Brixton.
Can we just change to Brixton?

* https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-dependencies/2022.0.1/versions

### Run It Again

```shell
docker compose up -d db
```

```shell
export DB_IP=localhost:15432
```


```shell
mvn flyway:migrate generate-resources generate-sources package -e \
  -Ddb.url="jdbc:postgresql://${DB_IP}/kw" \
  -Dspring.datasource.url=jdbc:postgresql://${DB_IP}/kw \
  -Dspring.datasource.username=docker \
  -Dspring.datasource.password=docker
```

```shell
sdk install java 8.0.412-librca
```

```shell
sdk use java 8.0.412-librca
```

```shell
nvm install  6
```

```shell
nvm use 6
```

## Disabled Frontend Plugin install of NodeJS

```shell
[INFO] Installing node version v6.10.3
[INFO] Downloading https://nodejs.org/dist/v6.10.3/node-v6.10.3-darwin-arm64.tar.gz to /Users/joostvdg/.m2/repository/com/github/eirslett/node/6.10.3/node-6.10.3-darwin-arm64.tar.gz
[INFO] No proxies configured
[INFO] No proxy was configured, downloading directly
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.197 s
[INFO] Finished at: 2024-06-10T20:14:54+02:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal com.github.eirslett:frontend-maven-plugin:1.5:install-node-and-yarn (install node and yarn) on project keep-watching-be: Could not download Node.js: Could not download https://nodejs.org/dist/v6.10.3/node-v6.10.3-darwin-arm64.tar.gz: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target -> [Help 1]
org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal com.github.eirslett:frontend-maven-plugin:1.5:install-node-and-yarn (install node and yarn) on project keep-watching-be: Could not download Node.js: Could not download https://nodejs.org/dist/v6.10.3/node-v6.10.3-darwin-arm64.tar.gz
    at org.apache.maven.lifecycle.internal.MojoExecutor.doExecute2 (MojoExecutor.java:333)
    at org.apache.maven.lifecycle.internal.MojoExecutor.doExecute (MojoExecutor.java:316)
```

* Manually run `npm install`
* Running `yarn install` fails with `Error: unable to get local issuer certificate`, see below

```shell
❯ yarn install
yarn install v1.22.22
info No lockfile found.
[1/4] 🔍  Resolving packages...
error Error: unable to get local issuer certificate
    at Error (native)
    at TLSSocket.<anonymous> (_tls_wrap.js:1092:38)
    at emitNone (events.js:86:13)
    at TLSSocket.emit (events.js:185:7)
    at TLSSocket._finishInit (_tls_wrap.js:609:8)
    at TLSWrap.ssl.onhandshakedone (_tls_wrap.js:439:38)
info Visit https://yarnpkg.com/en/docs/cli/install for documentation about this command.
```

```shell
npm install --save-dev webpack-cli@2.1.5
npm install --global webpack@2.7.0
```

### Build Frontend

We install the dependencies via `yarn`, v1.22.22 should work.

```shell
yarn install
```

To build the frontend, we need to run webpack, note, without any arguments.

```shell
webpack
```

### Disabled Frontend Plugin

It really can't download those version anymore.

### Disabled JKS Encryption for Cloud Config Server

In `bootstrap.yml`:

```yaml
#encrypt:
#  keyStore:
#    location: classpath:/config-server.jks
#    password: NotThePassword
#    alias: config-server-key
#    secret: AlsoNotThePassword
```

### Explicitly Set Bootstrap Version

Orignally we used the Bootstrap version 3.x.
Which as the time was the latest, so the version was not specified.

We need to specify the version, as the latest version is 5.x.

* https://getbootstrap.com/docs/3.4/getting-started/#download

```html
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.4.1/dist/css/bootstrap.min.css" integrity="sha384-HSMxcRTRxnN+Bdg0JdbxYKrThecOKuH5zCYotlSAcp1+c8xmyTe9GYg1l9a69psu" crossorigin="anonymous">

<!-- Optional theme -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.4.1/dist/css/bootstrap-theme.min.css" integrity="sha384-6pzBo3FDv/PJ8r2KRkGHifhEocL+1X2rVCTTkUfGk7/0pbek5mMa1upzvWbrUbOZ" crossorigin="anonymous">

<!-- Latest compiled and minified JavaScript -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@3.4.1/dist/js/bootstrap.min.js" integrity="sha384-aJ21OjlMXNL5UyIl/XNwTMqvzeRMZH2w8c5cRVpzpU8Y5bApTppSuUkhZXN0VxHd" crossorigin="anonymous"></script>
```


### Removed Spring JooQ Starter Version

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jooq</artifactId>
<!--            <version>2.2.6.RELEASE</version>-->
</dependency>
```

### Reverted JOOQ Version Change

From 3.14.4 to 3.9.3.

### Removed Release Date field

The Movie DTO has a field `releaseDate` which is a `LocalDate`.

Unfortunately, the Jackson deserialization of `LocalDate` is not working.

So I've commented out the field for now.
We can now create movie entries in a WatchList.

## Run It

```shell
docker compose up db -d
```

```shell
export DB_IP=localhost:15432
```

```shell
yarn install
```

```shell
webpack
```

```shell
mvn -e spring-boot:run \
    -Dspring.profiles.active=local \
    -Ddb.url=jdbc:postgresql://${DB_IP}/kw \
    -Dspring.datasource.url=jdbc:postgresql://${DB_IP}/kw \
    -Dspring.datasource.username=docker \
    -Dspring.datasource.password=docker
```

### With Docker

#### Build Image

* Separate Dockerfile from the Heroku build
* Separate run script from the Heroku build

#### Run With Compose

* need to specify DB params
* need to specify platform?

### Run with Tanzu Application Service (CloudFoundry)

* First have to migrate to a new PostgreSQL service
