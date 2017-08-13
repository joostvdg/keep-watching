# Database Setup

For the database configuration, the following requirements were set:

* automatically update the database from build
* automatically keep database and ORM in sync from build
* database can be maintained via SQL scripts only
* open source database
* database of which you can get free (or near free) instances
* capable of running in docker
* compatible with spring boot

## Tech Stack

* [Spring Boot](https://projects.spring.io/spring-boot/)
* [JOOQ](https://www.jooq.org/)
* [JOOQ Generator](https://www.jooq.org/doc/3.7/manual/code-generation/codegen-configuration/)
* [FlyWay](https://flywaydb.org/)
* [PostGreSQL](https://www.postgresql.org/)

## Configuration

### InitialConfiguration.java

Configuration class for JOOQ to make sure the connection to postgresql will work on every platform.

```java
@Configuration
public class InitialConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider
                (new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public DefaultDSLContext dsl() {
        return new DefaultDSLContext(configuration());
    }

    public DefaultConfiguration configuration() {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();

        jooqConfiguration.set(connectionProvider());
        Settings settings =new Settings();
        settings.setRenderNameStyle(RenderNameStyle.LOWER);
        settings.setRenderSchema(true);
        jooqConfiguration.set(settings);
        jooqConfiguration.set(new DefaultExecuteListenerProvider(new ExceptionTranslator()));

        return jooqConfiguration;
    }

}
```

### pom.xml

```xml
<properties>
    <flyway.baseline-on-migrate>true</flyway.baseline-on-migrate>
    <postgres.driver.version>9.2-1002.jdbc4</postgres.driver.version>
    <db.url>jdbc:postgresql://localhost:5432/kw</db.url>
    <db.user>docker</db.user>
    <db.password>docker</db.password>
    <flyway.schemas>public</flyway.schemas>
</properties>
```


#### FLYWAY 

For updating the Database via DB Migrations.
 
```xml
 <plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <configuration>
        <sqlMigrationSeparator>__</sqlMigrationSeparator>
        <locations>
            <location>filesystem:src/main/resources/db/migration</location>
        </locations>
        <url>${db.url}</url>
        <user>${db.user}</user>
        <password>${db.password}</password>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>9.4-1206-jdbc42</version>
        </dependency>
    </dependencies>
    <executions>
    <execution>
        <id>integration-test-database-setup</id>
        <phase>initialize</phase>
            <goals>
                <goal>clean</goal>
                <goal>migrate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```


#### JOOQ 

For generating Active Record classes from Database.

```xml
<plugin>
    <groupId>org.jooq</groupId>
    <artifactId>jooq-codegen-maven</artifactId>
    <version>${org.jooq.version}</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <!-- Manage the plugin's dependency. In this example, we'll use a PostgreSQL database -->
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>9.4-1206-jdbc42</version>
        </dependency>
    </dependencies>

    <!-- Specify the plugin configuration.
         The configuration format is the same as for the standalone code generator -->
    <configuration>

        <!-- JDBC connection parameters -->
        <jdbc>
            <driver>org.postgresql.Driver</driver>
            <url>${db.url}</url>
            <user>${db.user}</user>
            <password>${db.password}</password>
        </jdbc>

        <!-- Generator parameters -->
        <generator>
            <name>org.jooq.util.DefaultGenerator</name>
            <database>
                <name>org.jooq.util.postgres.PostgresDatabase</name>
                <includes>.*</includes>
                <excludes></excludes>
                <inputSchema>public</inputSchema>
            </database>
            <target>
                <packageName>com.github.joostvdg.keepwatching.model</packageName>
                <directory>gensrc/main/java</directory>
            </target>
        </generator>
    </configuration>
</plugin>
```


#### Build Helper

For adding generated sources (Active Record tabel classes)  from JOOQ to the sources list.

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <executions>
        <execution>
            <phase>process-sources</phase>
            <goals>
                <goal>add-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>gensrc/main/java</source>
                </sources>
            </configuration>
        </execution>
    </executions>
</plugin>    
```

### application.yml

```yaml
spring:
    datasource:
      url: localhost:5432/kw
      username: docker
      password: docker
```

### Docker Compose

```yaml
version: '2'
services:
  db:
    image: postgres
    expose:
      - 5432
    ports:
      - 15432:5432
    environment:
      - POSTGRES_PASSWORD=docker
      - POSTGRES_USER=docker
      - POSTGRES_DB=kw
  db-local:
    image: postgres
    expose:
      - 5432
    ports:
      - 15432:5432
    environment:
      - POSTGRES_PASSWORD=docker
      - POSTGRES_USER=docker
      - POSTGRES_DB=kw
    volumes:
      - /wolkje/volumes/kw/postgres:/var/lib/postgresql/data

  backend:
    image: caladreas/keep-watching-be
    expose:
      - 8080
    ports:
      - 8080:8080
    links:
        - db
```

## Way of Working

The process of change goes as follows:

* make sure the default postgresql database is up
* create a db migration in src/main/resources/db.migration
    * V{number}__{description_of_change}.sql format
* run Flyway migration to apply database changes
* run JOOQ generation to generate the classes of the active records (database tables)
* run maven build helper to make these generated class part of the source files
* compile normally
* run spring boot to test working database/application

```bash
docker-compose up db-local
DB_IP=$(docker inspect --format '{{.NetworkSettings.Networks.keepwatching_default.IPAddress}}' keepwatching_db_1)
mvn clean test package -P db -Dspring.profiles.active=compose -Ddb.url=jdbc:postgresql://${DB_IP}:5432/kw -Dspring.datasource.url=jdbc:postgresql://${DB_IP}:5432/kw -Dspring.datasource.username=docker -Dspring.datasource.password=docker 
```


## Resources

* [Flyway migrations during Maven build](https://blog.codecentric.de/en/2017/01/flyway-tutorial-execute-migrations-using-maven/)
* [Introduction to Spring & JOOQ](http://www.baeldung.com/jooq-with-spring)
* [JOOQ & Spring Boot](https://www.javacodegeeks.com/2016/03/springboot-working-jooq.html)
* [JOOQ & Spring Boot & Flyway](https://medium.com/@readsethu/jooq-flyway-spring-boot-and-gradle-44a8d3f289)
* [JOOQ & Flyway](https://blog.jooq.org/2014/06/25/flyway-and-jooq-for-unbeatable-sql-development-productivity/)
