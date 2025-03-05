# LimiterX

LimiterX is a powerful yet simple rate-limiting library for Java and Spring Boot. It supports both **fixed window** and **sliding window** strategies and comes with a Spring Boot starter for easy integration.

## Getting Started

### 1. Add LimiterX to Your Project

Include the dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.d4rckh</groupId>
    <artifactId>limiterx-spring-boot-starter</artifactId>
    <version>0.0.4</version>
</dependency>
```

### 2. Enable LimiterX

Simply annotate any `@Configuration` class:

```java
@Configuration
@EnableLimiterX
public class AppConfig { }
```

### 3. Apply Rate Limits

#### Basic Rate Limiting

> Limits access to a method for **all requests**, regardless of the client:

```java
@GetMapping
@RateLimited(
    maximumRequests = 2,
    windowSize = 10, // seconds
    blockFor = 1000 // optional: block duration in seconds
)
public String hello() {
    return "Hello!";
}
```

#### Limit by IP Address

> Uses `X-Forwarded-For` if available; otherwise, falls back to the client's remote address:

```java
@GetMapping
@RateLimited(
    key = IPExtractor.class,
    maximumRequests = 2,
    windowSize = 10
)
public String hello() {
    return "Hello!";
}
```

#### Limit by Username

> Restricts access based on the authenticated user's username (requires Spring Security):

```java
@GetMapping
@RateLimited(
    key = UsernameExtractor.class,
    maximumRequests = 2,
    windowSize = 10
)
public String hello() {
   return "Hello!";
}
```

## Key Extractors

Key extractors define how requests are grouped for rate limiting:

- **NoopExtractor** *(default)* – No key extraction; all requests share the same limit.
- **IPExtractor** – Uses the client’s IP address.
- **UsernameExtractor** – Uses the authenticated username from Spring Security.

## Key Expressions

Make a key using a Spring expression, only the beans implementing `KeyExtractor` are loaded using their class name.

### Example

> This will combine the UsernameExtractor with the IPExtractor. Warning: if the user is not authenticated, the key will only be formed using the IP

```java
@GetMapping
@RateLimited(
    keyExpression = "{#UsernameExtractor.extract(), #IPExtractor.extract()}",
    maximumRequests = 2,
    windowSize = 10
)
public String hello() {
    return "Hello!";
}
```

## Handling Null Keys

When the extracted key is `null`, you can choose how LimiterX handles it:

- **LIMIT** – Uses a shared key for all such requests.
- **FORBID** – Blocks requests with `null` keys.
- **AUTO** – Uses `LIMIT` for `NoopExtractor`, otherwise `FORBID`.

## Storage Options

By default, LimiterX stores rate-limiting data in **Redis**. To switch to in-memory storage, add this property:

```properties
limiterx.storage=memory
```

## Creating a Custom Key Extractor

Need custom rate-limiting logic? Implement `KeyExtractor` in your own class:

> You can use these in the keyExpression parameter!

```java
@Component
public class IPExtractor implements KeyExtractor {

    private final ObjectFactory<HttpServletRequest> requestFactory;

    @Autowired
    public IPExtractor(ObjectFactory<HttpServletRequest> requestFactory) {
        this.requestFactory = requestFactory;
    }

    public String extract() {
        String xForwardedForHeader = requestFactory.getObject().getHeader("X-Forwarded-For");
        return xForwardedForHeader == null ? httpRequest.getRemoteAddr() : xForwardedForHeader;
    }
}
```

## Custom Key Extractor with parameters for use in expressions

```java
@Component
public class MyExtractor implements KeyExtractor {

    private final ObjectFactory<HttpServletRequest> requestFactory;

    @Autowired
    public IPExtractor(ObjectFactory<HttpServletRequest> requestFactory) {
        this.requestFactory = requestFactory;
    }

    public String extract(String headerName) {
        return requestFactory.getHeader(headerName);
    }
}

@GetMapping
@RateLimited(
    keyExpression = "#MyExtractor.extract('something')",
    maximumRequests = 2,
    windowSize = 10
)
public String hello() {
    return "Hello!";
}
```


## License

LimiterX is open-source and licensed under the MIT License.

---

Enjoy rate limiting with **LimiterX**! 🚀

