# Spring & Spring Boot Config

This is a Spring Boot application generated via [Spring Initialzr](https://start.spring.io/). 
It is a web application build with Maven, using Spring Security, MVC, web (thymeleaf) and Cloud Config server.  

For an overview how these work, please take a look at the following tutorials:
 
* [Spring Boot](https://spring.io/guides/gs/spring-boot/)
* [Spring Rest services](https://spring.io/guides/gs/rest-service/)
* [Spring Boot and ReactJS](https://spring.io/guides/tutorials/react-and-spring-data-rest/)
* [Spring Boot and Docker](https://spring.io/guides/gs/spring-boot-docker/)
* [Spring Boot and Centralized Configuration](https://spring.io/guides/gs/centralized-configuration/)
* [Spring Boot and Swagger2](http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)

For the specifics for ReactJS, Database/ORM configuration or security configuration: please look at the individual pages.

## Jackson Config

We also want to get DateTime objects from Java 8 back as a proper timestamp, so we have to supply additional jackson configuration.

Another thing to note, is that we do not want to fail on JSON payloads that have additional properties.

### JacksonConfig.java

```java
@Configuration
public class JacksonConfig {
    /**
     * Instantiates a new jackson config.
     *
     */
    @Bean(name = "MyObjectMapper")
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return objectMapper;
    }
}
```

## Swagger2 config

### Application.java

```java
@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = "com.github.joostvdg.keepwatching")
@EnableTransactionManagement
@EnableAutoConfiguration
@EnableOAuth2Client
@EnableConfigServer
public class Application extends WebSecurityConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Controller example

As you can see with this hello world controller, not additional configuration is needed for Swagger to pick it up.

The only thing this needs is to be a @Controller or @RestController with a @RequestMapping on the class or a method.

```java
@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Collection<Movie>> getTools(){
        logger.info("Movies::GET");
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie(1l, "Logan"));
        movies.add(new Movie(2l, "John Wick 2"));
        return ResponseEntity.ok().body(movies);
    }
}

```
