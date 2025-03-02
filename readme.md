# LimiterX

LimiterX is a Java rate limiter that supports fixed window and sliding window strategies. It provides a Spring Boot starter with intuitive annotations for effortless configuration.

## Getting Started with the Spring Boot Starter

### 1. Add the Dependency

```xml
<dependency>
    <groupId>io.d4rckh</groupId>
    <artifactId>limiterx-spring-boot-starter</artifactId>
    <version>0.0.3</version>
</dependency>
```

### 2. Enable LimiterX

Annotate any `@Configuration` class to enable LimiterX:

```java
@Configuration
@EnableLimiterX
public class GeneralConfig { }
```

### 3. Start Limiting Methods!

#### Basic Rate Limiting

> **This will treat all requests as coming from the same client!** To limit by IP scroll below

```java
@GetMapping
@RateLimited(
    maximumRequests = 2,
    windowSize = 10, // seconds
    blockFor = 1000 // seconds; optional blockFor parameter
)
public String hello() {
    return "hello";
}
```

#### IP-Based Limiting

LimiterX can extract the IP from `X-Forwarded-For` or use the actual remote address:

```java
@GetMapping
@RateLimited(
    key = IPExtractor.class,
    maximumRequests = 2,
    windowSize = 10
)
public String hello() {
    return "hello";
} 
```

#### Username Limiting

```java
// Will use the UserDetails principal to get the Username, will throw an error if the user is not authenticated
@GetMapping
@RateLimited(
        key = UsernameExtractor.class,
        maximumRequests = 2,
        windowSize = 10
)
String hello() {
   return "hello";
}

// Username Limiting but will use the same key for unauthenticated function calls (won't throw any errors if the key is null)
@GetMapping
@RateLimited(
        key = UsernameExtractor.class,
        maximumRequests = 2,
        windowSize = 10,
        nullKeyStrategy = NullKeyStrategy.LIMIT
)
String hello() {
   return "hello";
}
```

## Available Key Extractors

- **NoopExtractor**: Default extractor; does not return a key, placing all users in a single bucket.
- **IPExtractor**: Uses `X-Forwarded-For` if available; otherwise, retrieves the remote address from `HttpServletRequest`.
- **UsernameExtractor**: For use with Spring Security; extracts the username from the authentication principal (must be an instance of `UserDetails`).

## Null Key Strategies

- **Limit**: if the key is null, it will limit using the same key all requests
- **Forbid**: won't allow function calls if the key is null
- **Auto**: will use `Limit` if key is NoopExtractor, otherwise `Forbid`

## Storage Configuration

By default, LimiterX uses **Redis** for storage. To use an in-memory storage backed by a **ConcurrentHashMap**, set the following property:

```properties
limiterx.storage = memory
```

## Creating a Custom Key Extractor

You can implement your own key extractor by creating a class that implements `KeyExtractor`.

### Example: IP-Based Key Extractor

```java
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

## License

LimiterX is open-source and available under the MIT License.

---

Enjoy rate limiting with LimiterX!
