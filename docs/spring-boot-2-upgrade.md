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

## Upgrade to Spring Boot 2.0

### Upgraded Dependencies

* Upgraded to spring boot 1.5.22 RELEASE
* Upgraded to spring boot 2.0.0 RELEASE
* Used spring-boot-starter-json replacing existing Jackson dependencies and remove starter-web
https://github.com/Azure/azure-sdk-for-java/wiki/Spring-Versions-Mapping
https://www.wimdeblauwe.com/blog/2018/2018-08-30-tip-on-migration-to-spring-boot-2-when-using-flyway/

Used commands to check build only: mvn -DskipTests package

* Replaced test runner SpringJUnit4ClassRunner
Tested with mvn test -Dtest="MoviesServiceTest#findAllMovies"

Related guides:
https://github.com/spring-guides/tut-spring-boot-oauth2/blob/main/two-providers/src/main/java/com/example/SocialApplication.java
https://github.com/spring-guides/tut-spring-boot-oauth2/blob/main/logout/src/main/java/com/example/SocialApplication.java
https://github.com/spring-guides/tut-spring-boot-oauth2/blob/main/simple/pom.xml
https://spring.io/guides/tutorials/spring-boot-oauth2
https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Migration-Guide

* Removed related spring-cloud, oauth2 and rsa dependencies since are not needed anymore
* Removed spring cloud in dependency manager
* TODO: Still we haven't fixed authentication and httpsecurity config.
* Migrated code to new OAuthUser principal

## Upgrade to Spring Boot 2.2.2 & Java 11

The GitHub OAuth2 examples we found, do not work for Spring Boot 2.0, but they do work for Spring Boot 2.2.2.
So we want to upgrade to 2.2.2, this also means we can upgrade to Java 11, as some of the plugins we use depend on it.

* Upgraded to Spring Boot 2.2.2 RELEASE
* Upgraded to Java 11
* Used spring-boot-starter-json replacing existing Jackson dependencies and remove starter-web
* Replaced test runner SpringJUnit4ClassRunner
* Removed related spring-cloud, oauth2 and rsa dependencies since are not needed anymore
* Removed spring cloud in dependency manager
* Migrated code to new OAuthUser principal
* Removed release date field from Movie DTO
* Removed JKS encryption for Cloud Config Server
* Disabled Swagger and Spring Fox
  * these are not working with Spring Boot 2.2.2, and we want the end result of Spring Boot 3, which works with a different library to achieve the same
  * we had to remove some annotations in the Controllers, as the classes are not available anymore
* Change Flyway to match version of Spring Boot, as it now manages that version
  * which is 6.0.8

### Response On User Endpoint

On the GitHub login:

```json
{"name":"539630","principle":"Joost van der Griendt"}
```

### Update Login Setup

* update login page, change to example from the guide: https://github.com/spring-guides/tut-spring-boot-oauth2/blob/main/two-providers/
* update the authentication mechanism, also from the guide: https://github.com/spring-guides/tut-spring-boot-oauth2/blob/main/two-providers/
* updated the mechanism to register the user -> at the authenticated endpoint, we register the user if they don't already exist
* added the Apache Commons Lang 3 library, as it came with one of the dependencies we removed, we but we still need it


## Upgrade to 2.5

https://github.com/jOOQ/jOOQ/blob/version-3.16.0-branch/jOOQ-examples/jOOQ-spring-boot-example/src/main/java/org/jooq/example/spring/service/DefaultBookService.java#L16
https://github.com/jOOQ/jOOQ/blob/version-3.16.0-branch/jOOQ-examples/jOOQ-spring-boot-example/src/main/java/org/jooq/example/spring/Config.java


### Upgrade Dependencies

* Upgraded to spring boot 2.5.6
* Remove spring boot starter jpa dependency
* Jooq dependency update and version controlled by spring boot
* Removed nhibernate dependencies
* Jooq version relying in spring boot
* Add jakarta persistence dependency for sql Datasource


### Code changes

* Cleaned up of jooq configurations and simplified config with autowired dsl
* Migrated to junit 5 and new assertions library in tests
* Auth Principal defensive coding due to missing properties in accounts
* Rely on jooq starter and not manual coordination between jpa and jooq
* webpack removed deprecated debug property
* Jooq spring properties file update

### Update JUnit Test

* Update Junit test when asserting for Exceptions
* https://www.baeldung.com/junit-assert-exception

## Upgrade Postgresql

We upgraded the PostgreSQL database to version 16.
* updated the docker-compose file to use the new version
* update the properties in the pom.xml's (parent and child)

## Upgrade To Spring Boot 2.7

We follow the guidance from the Spring team: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide.

We want to end up at Spring Boot 3.3, so we need to go through the steps.
First, we need to upgrade to Spring Boot 2.7.

* update parent pom to Spring Boot 2.7.18
* update Java version to 17
* replace the oauth configuration in application.yml with the new configuration

### Upgrade Spring Security to 5.8

In the Spring Upgrade Guide, we get the recommendation to upgrade to Spring Security 5.8 before upgrading Spring Boot.

> The Spring Security team have released Spring Security 5.8 to simplify upgrading to Spring Security 6.0. Before upgrading to Spring Boot 3.0, consider upgrading your Spring Boot 2.7 application to Spring Security 5.8.

We then follow these guides: 
* https://docs.spring.io/spring-security/reference/6.0/migration/index.html
* https://docs.spring.io/spring-security/reference/5.8/migration/index.html

What we did:
* upgraded Spring Security to 5.8.15
* replaced the WebSecurityConfigurerAdapter with a Configuration class
  * as described here: https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter

## Upgrade to Spring Boot 3

We follow the guidance from the Spring team: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide.

* upgrade to Spring Boot 3.0
* remove Jolokia
  * the version is no longer managed via Spring Boot, and it doesn't look like we need it
* remove Spring Security version override
* replace antMatchers with authorizeRequests
  * https://www.baeldung.com/spring-security-migrate-5-to-6
* replace javax.annotation with jakarta.annotation
* properties migrator gave a warning
  * recommended to use by the migration guide
  * for more info, see: https://www.baeldung.com/spring-boot-properties-migrator
* seems our Thymeleaf configuration is broken
  * we need to fix the Thymeleaf configuration
* welcome page doesn't load
  * probably due to the change in the security configuration

### Fix Property Migrator Warnings

> The use of configuration keys that have been renamed was found in the environment:
>
> Property source 'Config resource 'class path resource [application.yml]' via location 'optional:classpath:/'':
> Key: spring.datasource.continue-on-error
> Line: 4
> Replacement: spring.sql.init.continue-on-error
>
> Each configuration key has been temporarily mapped to its replacement for your convenience. To silence this warning, please update your configuration to use the new keys.

It looks like all we have to do, is replace `spring.datasource.continue-on-error` with `spring.sql.init.continue-on-error` in the `application.yml` file.

### Fix Thymeleaf Configuration

> 2024-06-25T14:37:24.524+02:00  WARN 25145 --- [  restartedMain] ion$DefaultTemplateResolverConfiguration : Cannot find template location: classpath:/templates/ (please add some templates, check your Thymeleaf configuration, or set spring.thymeleaf.check-template-location=false)

Retrieving the version, we get `3.1.2`:

```shell
 mvn dependency:tree | grep thymeleaf
[INFO] +- org.springframework.boot:spring-boot-starter-thymeleaf:jar:3.0.13:compile
[INFO] |  \- org.thymeleaf:thymeleaf-spring6:jar:3.1.2.RELEASE:compile
[INFO] |     \- org.thymeleaf:thymeleaf:jar:3.1.2.RELEASE:compile
```

We fix the warning is by setting the property in the `application.yml`.

### Fix Security Configuration

When we go to the main page, we get a Warning in the log:

>2024-06-25T14:47:43.021+02:00  WARN 28326 --- [nio-8080-exec-1] o.s.w.s.h.HandlerMappingIntrospector     : Cache miss for FORWARD dispatch to '/index.html' (previous null). Performing MatchableHandlerMapping lookup. This is logged once only at WARN level, and every time at TRACE.

And when we go to the main page, we get a 401 error.

There's two things broken:

1. we should get a proper error page
1. we should be able to access the main page without logging in

We'll first focus on being able to view our login page.

### Fix Authentication

The gist, is that Spring Security 6.0 has a new way of configuring the security.

* We need to replace the `WebSecurityConfigurerAdapter` with a `SecurityFilterChain`.
* We need to replace the `antMatchers` with `authorizeRequests`.
* We need to enable Web Security in the configuration, via `@EnableWebSecurity`.
* We need to include starter-web dependency.
* We need to configure the OAuth2 login.

We start by creating a new configuration class, `SecurityConfiguration`.
And we add the `@EnableWebSecurity` annotation.

We then add a `SecurityFilterChain` bean, which configures the security.
The authorizeRequests method is used to configure the security, but the matcher is not working as expected (different Regex).
In order to make it easier, we've put all the endpoints that need to be secured under `/api/**`.

The end result is this:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2Login(withDefaults());
        return http.build();
    }
}
```
References:

* https://www.baeldung.com/spring-security-migrate-5-to-6
* https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide
* https://stackoverflow.com/questions/52029258/understanding-requestmatchers-on-spring-security

With these changes, we got almost everything working.
All the Get requests work, but with any Post or Put request, we get a 403 forbidden.

It turns out, somewhere between Spring 5 and 6, several notorious breaches happened, and Spring Security tightened the defaults.
The main thing that is changed, is how CSRF protection is handled.

### CSRF Protection Change

The gist of the issue, is that before the CSRF token was added to the Cookie.
Older style JavaScript frameworks, such as the React version used by this App automatically pick up the token from the Cookie.

As the token is longer in the Cookie, this stops working.

There are several alternative ways this can be solved.
I chose to add the token to all response headers as a starting point via a `@ControllerAdvice` as a starting point.
As described in this section of the docs: https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-other.

After updating the JavaScript to match (see below), I realised I could limit this behaviour to the `UserController` only.

The end result is this:

```java
@ControllerAdvice(assignableTypes = UserController.class)
public class CsrfControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ModelAttribute
    public void getCsrfToken(HttpServletResponse response, CsrfToken csrfToken) {
        response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
    }
}
```

In addition, we need to tell the Spring Security configuration to use the CSRF token.

We do this, by adding the following to the `SecurityConfiguration` class:

```java
http
    .csrf((csrf) -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    )
```

In the JavaScript portion, we have to change how we handle the CSRF token.
Before, we could strip it from the Cookie, and this we could do always.

Unfortunately, with the new setup, we can only do this when the token is present in the header.
This means we need to be sure when we can extract the token.

While there are many ways this can be optimized, for now, the easiest is to verify we are authenticated via the `/authenticate` endpoint, and then extract the token.
And then we go on and do our Put or Post calls.

The changed JavaScript, now looks like this:

```javascript
handleSubmit1(event) {
        event.preventDefault();

        console.log("fetching authentication data");
        fetch('/authenticated', {
            headers: {'Accept': 'application/json'}
        }).then(response => {
            console.log("reading authentication data");
            console.log(response);
            console.log("reading headers");
            for (var pair of response.headers.entries()) { // accessing the entries
                console.log(pair[0] + ': ' + pair[1]);
                if (pair[0] === 'x-xsrf-token') { // key I'm looking for in this instance
                    console.log("=> Found X-Xsrf-Token: " + pair[1]);
                    this.setState({
                        csrf: pair[1] // saving that value where I can use it
                    })
                }
            }
        }).then(() => {this.createWatchList()});

    }

    createWatchList() {
        fetch( '/api/watchlist',{
            method: this.state.edit ? 'POST' : 'PUT',
            headers: {
                'Accept': 'application/json, application/xml, text/plain, text/html, */*',
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': this.state.csrf,
            },
            credentials: 'same-origin',
            mode: 'cors',
            redirect: 'follow',
            body: JSON.stringify({
                id: this.state.watchList.id,
                name: this.state.watchList.name
            })
        });

        this.setState({ showModal: false });
        this.setState({ watchList: {name: ''} });
    }
```

The `handleSubmit1` method is called when the form is submitted.
We then fetch the `/authenticated` endpoint, and extract the `X-Xsrf-Token` from the headers.
We then call the `createWatchList` method, which does the actual Put or Post call, which now has access to the CSRF token.

And then it still didn't work.

References:

* https://stackoverflow.com/questions/76682586/allow-cors-with-spring-security-6-1-1-with-authenticated-requests
* https://stevemiller.dev/2019/getting-response-headers-with-javascript-fetch/
* https://davy.ai/how-to-add-the-csrf-token-to-the-http-header-using-fetch-api-and-vanillajs/
* https://stackoverflow.com/questions/76047301/working-with-csrf-token-in-javascript-via-fetch-api
* https://docs.spring.io/spring-security/reference/5.8/migration/servlet/exploits.html#_defer_loading_csrftoken
* https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html
* https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-token-request-handler-custom
* https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-other
* https://stackoverflow.com/questions/74447118/csrf-protection-not-working-with-spring-security-6
* https://github.com/spring-projects/spring-security/issues/14450

### Still Not Working

While I was working on the CSRF token, I disabled the CORS configuration. `cors.disable()` in the `SecurityConfiguration` class.

We have to enable the CORS configuration before the CSRF protection works.
As I don't want to dive too much into configuring CORS, I looked for the easiest way to enable it.
Which is as follows:

* add `@CrossOrigin` to the Controller classes.
* add cors configuration to the `SecurityConfiguration` class.

The end result is this:

```java
http
    .cors(Customizer.withDefaults())
```

Now the CSRF protection works, and we can do Put and Post requests.

### Optimize Authentication Endpoint

Due to how the `/authenticated` endpoint is used, it now always goes to the Database.
This is a very expensive round trip, especially if we need to do this for every Put/Post request.

To speed things up a bit, I added a HashMap to the Service class, which stores the authenticated users.
That way, every user we've retrieved once during the run of the application, is usable without going to the Database.

!!! Danger
    This is a very naive implementation, and should be replaced with a proper caching mechanism.

### Fix Error Page

Due to the structure of the application, we have URLs in the frontend that are not available in the backend, or at least not in the same way.
So when you go to a URL that is not available in the backend, you get a 404 error, eventhough the Frontend shows you are already on this page.

So, to aid the user, we can add an error page which includes a redirect to the login page.
While this doesn't solve the problem, it at least reduced the confusion.

To do so, we re-enable the template scanning of the Thymeleaf templates, and add an error page.

We create the page `src/main/resources/templates/error.html`:

```html
<!DOCTYPE html>
<html>
    <body>
        <h1>Something went wrong! </h1>
        <h2>Our Engineers are on it</h2>
        <p>
            <a href="/">Go Home</a>
        </p>
    </body>
</html>
```

References:
* https://www.baeldung.com/spring-boot-custom-error-page

## Upgrade To Spring Boot 3.1

The latest version of Spring Boot 3.1 is 3.1.12.

### Failing Tests

The database tests are failing:

> Caused by: org.postgresql.util.PSQLException: ERROR: insert or update on table "watchlist" violates foreign key constraint "watchlist_user_id_fkey"
> Detail: Key (user_id)=(1) is not present in table "watcher".

One of the things we can do, is ensure we have a Watcher with ID 1 in the Database.

We do this, by creating a Database Migration file in the `src/main/resources/db/migration` folder.

To verify we can run a test and not fail, which includes the database migration with Flyway, JOOQ classes creation and compilation, we run this command:

```shell
mvn test -Dtest="ApplicationTests"  
```

This works, so let's try a single Database test:

```shell
mvn test -Dtest="WatchListServiceTest#cannotCreateTwoWatchListWithTheSameName"
```
This one succeeds. So let's try the other one:

```shell
mvn test -Dtest="WatchListServiceTest#shouldCreateAndDeleteWatchList"
```

Also succeeds, let's run them all:

```shell
# in the backend folder
make build-all
```

>[ERROR] Errors:
>[ERROR]   MoviesServiceTest.findAllMovies » BeanCreation Error creating bean with name 'com.github.joostvdg.keepwatching.service.MoviesServiceTest.ORIGINAL': Invocation of init method failed
>[ERROR]   MoviesServiceTest.shouldCreateNewMovie » BeanCreation Error creating bean with name 'com.github.joostvdg.keepwatching.service.MoviesServiceTest.ORIGINAL': Invocation of init method failed
>[ERROR]   MoviesServiceTest.shouldReturnMoviesForWatchListOne » BeanCreation Error creating bean with name 'com.github.joostvdg.keepwatching.service.MoviesServiceTest.ORIGINAL': Invocation of init method failed
>[ERROR]   WatchListServiceTest.canOnlyRetrieveOwnedAndSharedWatchLists:98 » DataIntegrityViolation jOOQ; SQL [insert into public.watchlist (name, user_id) values (?, ?) returning public.watchlist.id]; ERROR: insert or update on table "watchlist" violates foreign key constraint "watchlist_user_id_fkey"
>Detail: Key (user_id)=(1) is not present in table "watcher".
>[ERROR]   WatchListServiceTest.cannotCreateTwoWatchListWithTheSameName:83 » DataIntegrityViolation jOOQ; SQL [insert into public.watchlist (name, user_id) values (?, ?) returning public.watchlist.id]; ERROR: insert or update on table "watchlist" violates foreign key constraint "watchlist_user_id_fkey"
>Detail: Key (user_id)=(1) is not present in table "watcher".
>[ERROR]   WatchListServiceTest.getListOfWatchersSharingTheWatchList:119 » DataIntegrityViolation jOOQ; SQL [insert into public.watchlist (name, user_id) values (?, ?) returning public.watchlist.id]; ERROR: insert or update on table "watchlist" violates foreign key constraint "watchlist_user_id_fkey"
>Detail: Key (user_id)=(1) is not present in table "watcher".

The tests that fail:

* MoviesServiceTest.findAllMovies
* MoviesServiceTest.shouldCreateNewMovie
* MoviesServiceTest.shouldReturnMoviesForWatchListOne
* WatchListServiceTest.canOnlyRetrieveOwnedAndSharedWatchLists
* WatchListServiceTest.cannotCreateTwoWatchListWithTheSameName
* WatchListServiceTest.getListOfWatchersSharingTheWatchList

Could it be, we are deleting the watcher accidentally?

## Upgrade to Spring Boot 3.2

* Changed only the parent pom to spring boot to 3.2.7

## Upgrade to Spring Boot 3.3

* Changed only the parent pom to spring boot to 3.3.1

## Upgrade to Java 21

* Changed pom to Java 21

## Add Swagger/OpenAPI Docs

* add dependency

* And we're done, we can visit these urls:
* http://localhost:8080/swagger-ui/index.html
* http://localhost:8080/v3/api-docs

For completeness, we added the `@Operation` and `@ApiResponses` annotations to the Controllers.
As described in this [guide](https://www.baeldung.com/spring-rest-openapi-documentation).

### References

* https://spring.io/guides/gs/testing-restdocs
* https://spring.io/projects/spring-restdocs
* https://www.baeldung.com/spring-rest-openapi-documentation

## TODO

* re-do test with Spring + TestContainers
* re-do Spring Cloud Config server (with encryption)
* optimize startup performance
  * optimize performance with AOT transpilation
  * optimize performance with CDS/CRAC?
  * https://www.youtube.com/watch?v=zeY3Wg1ieqI
  * https://github.com/spring-tips/go-further-and-faster-with-spring-boot-3-3
* add OTEL for tracing

## Errata

There are some things we won't fix, as the application has always had these issues.

We can assume some of these are now expected behaviour (the app is seven years old), and if we want to resolve those, they should be handled independently.

* never properly implemented the Delete functionality
  * e.g., it doesn't handle cascade delete / relations
* there are errors when calling the Shared WatchList
* there is a race condition when the client calls authenticated too fast in a row, the user is not yet in the Database, so we end up trying to insert it twice
  * not an issue, but it pollutes the log
