# LimiterX

slick java rate limiting with spring support

## Using the Spring Boot Starter:

1. Add the dependency:
    ```xml
    <dependency>
        <groupId>org.d4rckh</groupId>
        <artifactId>limiterx-spring-boot-starter</artifactId>
        <version>you find the latest version ;)</version>
    </dependency>
    ```
2. Enable LimiterX using the annotation on any @Configuration class:
    ```java
    @Configuration
    @EnableLimiterX
    public class GeneralConfig { }
   ```
3. Start limiting methods!
    ```java
    @GetMapping
    @RateLimited(
        maximumRequests = 2,
        windowSize = 10,
        blockFor = 1000 // Optional blockFor parameter
    )
    String hello() {
        return "hello";
    } 
    
    // It's possible to limit per custom keys
    @GetMapping
    @RateLimited(
        value = /* language=SpEL */ "authentication.principal.userId",
        maximumRequests = 2,
        windowSize = 10
    )
    String hello() {
        return "hello";
    } 
   ```