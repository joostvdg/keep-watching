# Cloud Config

So Keep-Watching is a service that's run in the cloud, but must also be debuggable on a local setup.

The gives rise to a requirement about the configuration, which needs to be dynamically loaded based on the context.

Next up, it is using public services only for hosting (sources), building and running.
That makes it very difficult to keep things a secret so the secrets must be encrypted.

## Spring Cloud Config

For Keep-Watching, this is: [Keep-Config](https://github.com/joostvdg/keep-config).

For the external dynamic configuration loading, we utilize [Spring Cloud Config Server](https://cloud.spring.io/spring-cloud-config/spring-cloud-config.html).

This allows you to store configuration files in a git repository (local or remote) which contains configuration per profile.

For this to work, you need two spring applications and a repository:

* The application for which you want to create the configuration (the Client, Keep-Watching)
* A Spring application which acts as a Cloud Config Server (the Server, Keep-Config)
* Git repository with the configuration (Config)

For how to set this up, [there's a nice tutorial](https://spring.io/guides/gs/centralized-configuration/) from SpringSource.

### Keep-Config

Make sure its a spring boot app, that contains the cloud config server dependency.

#### pom.xml

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

#### Application.java

The application class should then enable the server with the annotation ```@EnableConfigServer```

```java
@SpringBootApplication
@EnableConfigServer
public class RestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }
}
```

And last but not least, it needs to configure where the configuration should come from in ```application.yml```

#### application.yml

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/joostvdg/config
        encrypt:
          enabled: false
```

### Keep-Watching

The client needs to configure that it uses a cloud config server, where it is and that requires a dependency on ```spring-cloud-starter-config```.

#### pom.xml

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

### bootstrap.yml

As the configuration for the application should be loaded before the applications starts, this configuration should be in the ```bootstrap.yml``` file.

```yaml
spring:
  application:
    name: keep-watching
  cloud:
    config:
      uri: http://localhost:8081

---
spring:
  profiles: heroku
  cloud:
    config:
      uri: https://keep-config.herokuapp.com
```

And depending on the active profile we will use a different server for this.

### Config

For Keep-Watching, this is: [Config](https://github.com/joostvdg/config).

THe only thing to do here to create configuration files per application for each profile.

* keep-watching-compose.properties: used when profile is compose
* keep-watching-heroku.properties: used when profile in heroku
* keep-watching.properties: default file used

These files are then just standard Java properties files.

## Encryption

For how to use the encryption with Spring Cloud Config, there's a great tutorial from [Baeldung](http://www.baeldung.com/spring-cloud-configuration).

### Java Encryption Strength 

Due to strong [export restrictions on cryptography in the U.S.A](https://en.wikipedia.org/wiki/Export_of_cryptography_from_the_United_States) 
and Java being from there, Java's default encryption strength is severely limited.

For secure encryption of secrets in public places, such as the configuration being in a GitHub repo, we need to use stronger algorithms.

There's an easy solution to this: download and "install" Java's [Java Cryptography Extension](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)(JCE).

#### Config Server on Heroku

As we run our Config Server directly on Heroku, our Heroku host needs to have the Unlimited Strength in place.

This is actually surprisingly simple, as can be seen [in this tutorial on heroku's devcenter](https://devcenter.heroku.com/articles/customizing-the-jdk).

Simply create a ```.jdk-overlay``` folder and "install" the JCE just as you would do locally.

#### Docker

As Keep-Watching runs in a Docker container, it will also need the JCE to be able to use the "Unlimited Strength Policy".

So we use a Docker base image which contains exactly that: [anapsix/alpine-java:8_jdk_unlimited](https://hub.docker.com/r/anapsix/alpine-java/). 

### Spring Configuration

We need to add a JKS and configure Spring to use this JKS.

This is exactly the same for the server and the client.

This is what is required in the ```application.yml```.

```yaml
encrypt:
  keyStore:
    location: classpath:/config-server.jks
    password: nothepassword
    alias: config-server-key
    secret: nothepassword
``` 

As to be expected, we do not use the actual username and password of the JKS in the sources.

This would undermine the safety of the encryption, see below how this is configured.

### Secret Encryption / Decryption

To encrypt and decrypt the values, we can use the keep-config application.

To encrypt: `` curl -X POST --data-urlencode d3v3L http://root:s3cr3t@localhost:8081/encrypt``

To decrypt: `` curl -X POST --data-urlencode d3v3L http://root:s3cr3t@localhost:8081/decrypt``

Once you have a encrypted value, you want to store it encrypted in the config repository.

In order to tell the config server this value is encrypted, you have to add a special marker; ```{cipher}```

```properties
message=Hoi hoi hoi
user.password={cipher}AgAuqFORZF2ls7XmjQxotluoVXL7M8kEM8OV8Z9/xBPReuVMCbF5Krcd2qNQRq2/l6gTBrqcQXdy/nnv4dHxxGfDU4fxOAL+6YjPqLpZ13N9UYG8sKBw9UjupltLR3S/xHGXBFPp67WC/OeZ7MLbLqa8chY9UWbSySFcK43kNuTKZYsHfeh6ZZt7rAkjzdLoIAC1k4t1YVZxn4Bx9c3gOEIV9ZH1va+AJHg09xRXslCApUklTx6RRTOPt7G+iRizKZe9cwlZwJXu5Niaujtv8Jo6B8HdCq6c5fh0N4Lvvfohb1pOX/drKJm56zRzklcn/Tz8/xAKS4GsPks++zWdhqJU+xVMqBTD7htglmU3j2VZs2YqrBcw5hojEwQPRgH0e6BiU+IxLCqUolaSmCRgWrtx/Yz+Ft6X8zq3Fa+ater3MhptP40LJDDRiA+Gathvp+YHf7SpToGEea4Mxcx547IwzDqigXgMxhhQwyvI6fzR5IZXxL1kY2mUgIyPpg+xCg2bx4lH9ufGtZCr8AYkjnsZc5LH6DGPaYeWmpYu+LuNuRxVP2OdH1UVXhLL+X35MZq9RBtSTK/9JU1WtRVdc7q+g7YbaE1DKnt/5zteX0sfQO7rs20ATMF5JLM3KglHm27Pv4RSQWl4CEUqtL0AhsE6/pxFaxpZ9LsvnNk5GZu/jPkZlduKyFJneJCG4lg4jc5CAMfuExv9Sx2NyKV4wpSP1Qs9VqyvnUA1BFtOL4nS19kRZigsIZDVBxWS6X5yWIk=
```

!!! warning
    If you would now request the values from the server, both will come in plain text.
    You have to disable the automatic decryption by the config server!

#### Disable automatic decryption in cloud config server

```yaml hl_lines="7 8"
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/joostvdg/config
        encrypt:
          enabled: false
```

#### Decrypt in client

When decryption is disabled in the server, we will have to decrypt in the client.

It will need the same JKS as was use for the encryption (see above).

For the decryption to work, one more thing is required: a dependency on ```spring-security-rsa```.

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-rsa</artifactId>
</dependency>
```

### JKS Secret & Password

Make sure we have an additional line for when we've detected we're running (the docker container) in Heroku: 

```bash
if [ "${DATABASE_URL}" ]; then
  # ...
  EXTRA_CONFIG="-Dencrypt.keyStore.secret=${KEYSTORE_SECRET} -Dencrypt.keyStore.password=${KEYSTORE_PASS} -Dspring.profiles.active=heroku"
fi
java ... ${EXTRA_CONFIG} -jar /app.jar
```

Make sure we add the secrets are available as environment variables, so the docker run will be able to use them. 

We do the same with building in CircleCI, by adding this to the test command (in test.sh).

```bash
mvn flyway:migrate generate-resources generate-sources package -e ... -Dencrypt.keyStore.secret=${JKS_SECRET} -Dencrypt.keyStore.password=${JKS_PASS} 
```


