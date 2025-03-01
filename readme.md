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
    
    // IP Limiting (will use X-Forwarded-Host, otherwise actual remote address from HttpServletRequest)
    @GetMapping
    @RateLimited(
        key = IPExtractor.class,
        maximumRequests = 2,
        windowSize = 10
    )
    String hello() {
        return "hello";
    } 
   ```

Available extractors:
- NoopExtractor: default key extractor, it will not return any key, causing all users to be put in one bucket
- IPExtractor: will use X-Forwarded-Host, otherwise actual remote address from HttpServletRequest
- UsernameExtractor: for use with Spring Security, it will extract the username from authentication principal security context (it must be an instance of UserDetails)

### Custom Key Extractor Example

```java
// This is actually the key extractor from the library
@Component
public class IPExtractor implements KeyExtractor {

    private final HttpServletRequest httpRequest;

    @Autowired
    public IPExtractor(ObjectFactory<HttpServletRequest> requestFactory) {
        this.httpRequest = requestFactory.getObject();
    }

    public String extract() {
        String xForwardedForHeader = httpRequest.getHeader("X-Forwarded-For");

        return xForwardedForHeader == null ? httpRequest.getRemoteAddr() : xForwardedForHeader;
    }
}
```