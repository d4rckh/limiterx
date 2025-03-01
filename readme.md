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
        value = IPExtractor.class,
        maximumRequests = 2,
        windowSize = 10
    )
    String hello() {
        return "hello";
    } 
   ```
   
### Custom IP Extractor

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

// For use with Spring Security
@Component
public class UsernameExtractor implements KeyExtractor {
   public String extract() {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      if ((principal instanceof UserDetails appUserPrincipal)) {
         return appUserPrincipal.getUsername();
      }

      return "";
   }
}
```