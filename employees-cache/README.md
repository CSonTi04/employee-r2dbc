# Employees Cache - Reactive Caching with Redis

A full-stack reactive Spring Boot application demonstrating REST API development with Spring WebFlux, R2DBC database access, and **reactive caching with Redis**.

## 📋 Overview

This project extends the `employees-r2dbc` example by adding **reactive caching** using Redis. It demonstrates the cache-aside pattern in a reactive application, showing how to reduce database load by caching frequently accessed data.

## 🎯 Learning Objectives

After working with this project, you should understand:
- Reactive caching with Redis and Spring Data Redis Reactive
- Cache-aside pattern implementation
- Configuring `ReactiveRedisTemplate`
- Custom serialization for Redis
- Cache warming and invalidation strategies
- Performance optimization with caching
- Integration of Redis with R2DBC

## 🛠️ Technology Stack

- **Java 21** - Modern Java LTS version
- **Spring Boot 4.0.0** - Application framework
- **Spring WebFlux** - Reactive web framework
- **Spring Data R2DBC** - Reactive database access
- **Spring Data Redis Reactive** - Reactive Redis operations
- **R2DBC PostgreSQL** - Reactive PostgreSQL driver
- **r2dbc-migrate 3.3.0** - Database migration tool
- **Redis** - In-memory data store for caching
- **Lettuce** - Reactive Redis driver
- **Lombok** - Reduce boilerplate code
- **Jakarta Validation** - Bean validation API
- **Maven** - Build and dependency management

## 📁 Project Structure

```
employees-cache/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── employees/
│   │   │       ├── EmployeesWebApplication.java  # Main application
│   │   │       ├── EmployeeController.java       # REST endpoints
│   │   │       ├── EmployeeService.java          # Business logic with caching
│   │   │       ├── EmployeeRepository.java       # R2DBC repository
│   │   │       ├── Employee.java                 # Entity (record)
│   │   │       ├── EmployeeDto.java              # Data Transfer Object
│   │   │       ├── ShortEmployeeDto.java         # Projection DTO
│   │   │       ├── CacheConfig.java              # Redis configuration
│   │   │       ├── EmployeeErrorHandler.java     # Global error handler
│   │   │       ├── Violation.java                # Validation error DTO
│   │   │       ├── CounterController.java        # Counter example
│   │   │       ├── HelloController.java          # Hello world example
│   │   │       └── HelloMessage.java             # Hello message DTO
│   │   └── resources/
│   │       ├── application.properties            # Configuration
│   │       └── db/
│   │           └── migration/
│   │               └── V1__employees.sql         # Database schema
│   └── test/
│       └── java/
│           └── employees/
│               └── EmployeesWebApplicationTests.java
├── employees.http                                # HTTP request examples
├── pom.xml
├── mvnw                                         # Maven wrapper (Unix)
└── mvnw.cmd                                     # Maven wrapper (Windows)
```

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8+ (or use included Maven wrapper)
- PostgreSQL 16+ (or Docker)
- Redis 7+ (or Docker)

### Database Setup

#### PostgreSQL with Docker

```bash
docker run --name postgres-employees-cache \
  -e POSTGRES_DB=employees \
  -e POSTGRES_USER=employees \
  -e POSTGRES_PASSWORD=employees \
  -p 5432:5432 \
  -d postgres:16
```

#### Redis with Docker

```bash
docker run --name redis-cache \
  -p 6379:6379 \
  -d redis:7-alpine
```

#### Using PowerShell (Windows)

```powershell
# PostgreSQL
docker run --name postgres-employees-cache `
  -e POSTGRES_DB=employees `
  -e POSTGRES_USER=employees `
  -e POSTGRES_PASSWORD=employees `
  -p 5432:5432 `
  -d postgres:16

# Redis
docker run --name redis-cache `
  -p 6379:6379 `
  -d redis:7-alpine
```

#### Using Docker Compose (Recommended)

Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: employees
      POSTGRES_USER: employees
      POSTGRES_PASSWORD: employees
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data

volumes:
  postgres-data:
  redis-data:
```

Run with:
```bash
docker-compose up -d
```

### Build and Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Or using Maven wrapper (Windows)
.\mvnw.cmd spring-boot:run
```

The application starts on **http://localhost:8080**

### Verify Services

```bash
# Check PostgreSQL
docker exec -it postgres-employees-cache psql -U employees -d employees -c "SELECT * FROM employees;"

# Check Redis
docker exec -it redis-cache redis-cli
> KEYS *
> GET <key-name>
> exit
```

## 🌐 API Endpoints

### Employee CRUD Operations with Caching

#### List All Employees

```http
GET http://localhost:8080/api/employees
```

**Response**: `200 OK` - Returns all employees (from database)

#### Get Employee by ID (with caching)

```http
GET http://localhost:8080/api/employees/1
```

**First request**: Fetches from database and stores in Redis cache
**Subsequent requests**: Fetches from Redis cache (much faster!)

**Response**: `200 OK`
```json
{
  "id": 1,
  "name": "John Doe"
}
```

#### Get Short Employee DTO

```http
GET http://localhost:8080/api/employees/1/short
```

Demonstrates projection - returns only specific fields

#### Create Employee

```http
POST http://localhost:8080/api/employees
Content-Type: application/json

{
  "name": "Jack Doe"
}
```

**Response**: `201 Created`

#### Update Employee

```http
PUT http://localhost:8080/api/employees/1
Content-Type: application/json

{
  "id": 1,
  "name": "John Smith"
}
```

**Response**: `200 OK`

**Note**: Should implement cache invalidation on update!

#### Delete Employee

```http
DELETE http://localhost:8080/api/employees/1
```

**Response**: `204 No Content`

**Note**: Should implement cache invalidation on delete!

## 💻 Code Examples

### Redis Configuration

```java
@Configuration(proxyBeanMethods = false)
public class CacheConfig {
    @Bean
    public ReactiveRedisTemplate<Long, Employee> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {
        
        ObjectMapper objectMapper = new ObjectMapper();
        RedisSerializer<Employee> redisSerializer = 
            new JacksonJsonRedisSerializer<>(objectMapper, Employee.class);
        
        RedisSerializationContext<Long, Employee> context = 
            RedisSerializationContext
                .<Long, Employee>newSerializationContext(RedisSerializer.string())
                .key(new GenericToStringSerializer<>(Long.class))
                .hashKey(new JacksonJsonRedisSerializer<>(Long.class))
                .value(redisSerializer)
                .hashValue(redisSerializer)
                .build();
        
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
```

### Service with Cache-Aside Pattern

```java
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;
    private final ReactiveRedisTemplate<Long, Employee> redisTemplate;

    public Mono<EmployeeDto> findById(long id) {
        return redisTemplate
                .opsForValue()
                .get(id)  // Try to get from cache
                .map(EmployeeService::toDto)
                .log()
                .switchIfEmpty(
                    // Cache miss - fetch from database
                    repository
                        .findDtoById(id, EmployeeDto.class)
                        .flatMap(dto -> 
                            // Store in cache for next time
                            redisTemplate
                                .opsForValue()
                                .set(id, toEntity(dto))
                                .thenReturn(dto)
                        )
                        .log()
                );
    }

    private static EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(employee.id(), employee.name());
    }

    private static Employee toEntity(EmployeeDto employeeDto) {
        return new Employee(employeeDto.id(), employeeDto.name());
    }
}
```

### Repository with Projections

```java
public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {

    @Query("select id, name from employees")
    Flux<EmployeeDto> findDtoAll();

    @Query("select id, name from employees where id = :id")
    <T> Mono<T> findDtoById(Long id, Class<T> clazz);
}
```

### DTOs

```java
// Full DTO
public record EmployeeDto(Long id, String name) {}

// Projection DTO
public record ShortEmployeeDto(Long id) {}
```

## ⚙️ Configuration

### application.properties

```properties
spring.application.name=employees-cache

# PostgreSQL R2DBC Configuration
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/employees
spring.r2dbc.username=employees
spring.r2dbc.password=employees

# Database Migration
r2dbc.migrate.resources=db/migration

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Optional: Redis connection pool
# spring.data.redis.lettuce.pool.max-active=8
# spring.data.redis.lettuce.pool.max-idle=8
# spring.data.redis.lettuce.pool.min-idle=0

# Optional: Redis timeout
# spring.data.redis.timeout=60000
```

### Database Schema

**V1__employees.sql**
```sql
CREATE TABLE IF NOT EXISTS employees (
    id SERIAL PRIMARY KEY, 
    name VARCHAR(255) NOT NULL
);
```

## 🎯 Key Features

### 1. Cache-Aside Pattern

The application implements the cache-aside (lazy loading) pattern:

1. **Read**: Check cache first, if miss, read from DB and populate cache
2. **Write**: Write to DB, invalidate/update cache

```java
// Cache-aside read
public Mono<EmployeeDto> findById(long id) {
    return redisTemplate.opsForValue().get(id)
        .switchIfEmpty(fetchFromDbAndCache(id));
}
```

### 2. Reactive Redis Operations

```java
// Get from cache
Mono<Employee> employee = redisTemplate.opsForValue().get(id);

// Set in cache
Mono<Boolean> result = redisTemplate.opsForValue().set(id, employee);

// Set with TTL
Mono<Boolean> result = redisTemplate.opsForValue()
    .set(id, employee, Duration.ofMinutes(10));

// Delete from cache
Mono<Boolean> result = redisTemplate.opsForValue().delete(id);

// Check if exists
Mono<Boolean> exists = redisTemplate.hasKey(id);
```

### 3. Custom Serialization

Uses Jackson for JSON serialization to Redis:

```java
RedisSerializer<Employee> redisSerializer = 
    new JacksonJsonRedisSerializer<>(objectMapper, Employee.class);
```

**Benefits**:
- Human-readable in Redis
- Easy debugging with `redis-cli`
- Cross-platform compatibility

### 4. DTO Projections

Different views of the same data:

```java
// Full DTO
@Query("select id, name from employees where id = :id")
Mono<EmployeeDto> findFullById(Long id);

// Projection - only ID
@Query("select id from employees where id = :id")
Mono<ShortEmployeeDto> findShortById(Long id);

// Generic projection
<T> Mono<T> findDtoById(Long id, Class<T> clazz);
```

## 🔍 Caching Strategies

### Read-Through Cache (Current Implementation)

```java
public Mono<EmployeeDto> findById(long id) {
    return redisTemplate.opsForValue().get(id)
        .switchIfEmpty(
            repository.findById(id)
                .flatMap(employee -> 
                    redisTemplate.opsForValue()
                        .set(id, employee)
                        .thenReturn(employee)
                )
        )
        .map(this::toDto);
}
```

### Write-Through Cache (Recommended Enhancement)

```java
public Mono<EmployeeDto> save(Mono<EmployeeDto> employeeDto) {
    return employeeDto
        .map(this::toEntity)
        .flatMap(repository::save)
        .flatMap(saved -> 
            // Update cache on write
            redisTemplate.opsForValue()
                .set(saved.id(), saved)
                .thenReturn(saved)
        )
        .map(this::toDto);
}
```

### Cache Invalidation on Delete

```java
public Mono<Void> deleteById(long id) {
    return repository.deleteById(id)
        .then(redisTemplate.opsForValue().delete(id))
        .then();
}
```

### Cache with TTL (Time To Live)

```java
public Mono<EmployeeDto> findById(long id) {
    return redisTemplate.opsForValue().get(id)
        .switchIfEmpty(
            repository.findById(id)
                .flatMap(employee -> 
                    redisTemplate.opsForValue()
                        .set(id, employee, Duration.ofMinutes(10))  // 10 min TTL
                        .thenReturn(employee)
                )
        )
        .map(this::toDto);
}
```

## 🧪 Testing

### Manual Testing with redis-cli

```bash
# Connect to Redis
docker exec -it redis-cache redis-cli

# View all keys
KEYS *

# Get a cached employee
GET 1

# Check TTL
TTL 1

# Delete a key
DEL 1

# Monitor all commands (useful for debugging)
MONITOR
```

### Performance Testing

```bash
# First request (cache miss) - slower
curl http://localhost:8080/api/employees/1

# Second request (cache hit) - much faster
curl http://localhost:8080/api/employees/1
```

**Expected Results**:
- First request: 50-100ms (database query)
- Cached request: 1-5ms (Redis lookup)

### Unit Testing with Embedded Redis

Add to `pom.xml`:
```xml
<dependency>
    <groupId>it.ozimov</groupId>
    <artifactId>embedded-redis</artifactId>
    <version>0.7.3</version>
    <scope>test</scope>
</dependency>
```

Test example:
```java
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeServiceCacheTest {
    
    @Autowired
    private EmployeeService service;
    
    @Autowired
    private ReactiveRedisTemplate<Long, Employee> redisTemplate;
    
    @Test
    void testCacheHit() {
        // First call - cache miss
        StepVerifier.create(service.findById(1L))
            .expectNextMatches(dto -> dto.id() == 1L)
            .verifyComplete();
        
        // Verify it's in cache
        StepVerifier.create(redisTemplate.hasKey(1L))
            .expectNext(true)
            .verifyComplete();
        
        // Second call - should be from cache
        StepVerifier.create(service.findById(1L))
            .expectNextMatches(dto -> dto.id() == 1L)
            .verifyComplete();
    }
}
```

## 📖 Common Patterns

### Pattern 1: Cache-Aside with Logging

```java
public Mono<EmployeeDto> findById(long id) {
    return redisTemplate.opsForValue().get(id)
        .doOnNext(e -> log.info("Cache hit for id: {}", id))
        .switchIfEmpty(
            Mono.defer(() -> {
                log.info("Cache miss for id: {}", id);
                return repository.findById(id)
                    .flatMap(employee -> 
                        redisTemplate.opsForValue()
                            .set(id, employee)
                            .doOnSuccess(r -> log.info("Cached employee: {}", id))
                            .thenReturn(employee)
                    );
            })
        )
        .map(this::toDto);
}
```

### Pattern 2: Bulk Cache Warming

```java
public Mono<Void> warmCache() {
    return repository.findAll()
        .flatMap(employee -> 
            redisTemplate.opsForValue()
                .set(employee.id(), employee)
        )
        .then();
}
```

### Pattern 3: Cache Invalidation

```java
public Mono<EmployeeDto> update(Long id, EmployeeDto dto) {
    return repository.findById(id)
        .flatMap(existing -> {
            existing.setName(dto.name());
            return repository.save(existing);
        })
        .flatMap(updated -> 
            // Invalidate cache on update
            redisTemplate.opsForValue().delete(id)
                .thenReturn(updated)
        )
        .map(this::toDto);
}
```

### Pattern 4: Conditional Caching

```java
public Mono<EmployeeDto> findById(long id, boolean useCache) {
    if (!useCache) {
        return repository.findById(id).map(this::toDto);
    }
    
    return redisTemplate.opsForValue().get(id)
        .switchIfEmpty(fetchFromDbAndCache(id))
        .map(this::toDto);
}
```

## 🐛 Common Pitfalls

### 1. Forgetting Cache Invalidation

```java
// ❌ WRONG - Cache becomes stale
public Mono<Employee> update(Long id, Employee employee) {
    return repository.save(employee);  // Cache not updated!
}

// ✅ CORRECT - Invalidate cache
public Mono<Employee> update(Long id, Employee employee) {
    return repository.save(employee)
        .flatMap(saved -> 
            redisTemplate.opsForValue().delete(id)
                .thenReturn(saved)
        );
}
```

### 2. Serialization Issues

```java
// ❌ WRONG - May fail with complex objects
new GenericJackson2JsonRedisSerializer();

// ✅ CORRECT - Type-safe serialization
new JacksonJsonRedisSerializer<>(objectMapper, Employee.class);
```

### 3. Not Handling Cache Failures

```java
// ❌ WRONG - Fails if Redis is down
public Mono<Employee> findById(long id) {
    return redisTemplate.opsForValue().get(id);  // Error if Redis down
}

// ✅ CORRECT - Fallback to database
public Mono<Employee> findById(long id) {
    return redisTemplate.opsForValue().get(id)
        .onErrorResume(e -> {
            log.warn("Redis error, falling back to DB", e);
            return repository.findById(id);
        });
}
```

### 4. Cache Stampede

```java
// ❌ WRONG - Multiple requests can cause DB overload
public Mono<Employee> findById(long id) {
    return redisTemplate.opsForValue().get(id)
        .switchIfEmpty(repository.findById(id));
}

// ✅ CORRECT - Use Reactor's cache() operator
private final Map<Long, Mono<Employee>> loadingCache = new ConcurrentHashMap<>();

public Mono<Employee> findById(long id) {
    return loadingCache.computeIfAbsent(id, k ->
        redisTemplate.opsForValue().get(id)
            .switchIfEmpty(repository.findById(id))
            .cache()  // Prevents multiple DB calls
    );
}
```

## 🚀 Performance Optimization

### 1. Connection Pooling

Configure Lettuce connection pool in `application.properties`:

```properties
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=2
spring.data.redis.lettuce.pool.max-wait=1000ms
```

### 2. Pipeline Operations

Batch multiple Redis operations:

```java
public Mono<Void> cacheMultiple(List<Employee> employees) {
    return redisTemplate.execute(connection -> 
        Flux.fromIterable(employees)
            .flatMap(emp -> 
                connection.stringCommands()
                    .set(emp.id().toString(), serialize(emp))
            )
            .then()
    );
}
```

### 3. Set Appropriate TTL

```java
// Short TTL for frequently changing data
.set(id, employee, Duration.ofMinutes(5))

// Long TTL for static data
.set(id, employee, Duration.ofHours(24))
```

## 🔍 Monitoring

### Redis Stats

```bash
# Connect to Redis
docker exec -it redis-cache redis-cli

# Get info
INFO stats

# Monitor commands
MONITOR

# Check memory usage
INFO memory

# Get slow log
SLOWLOG GET 10
```

### Application Metrics

Enable actuator for metrics:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Add to `application.properties`:
```properties
management.endpoints.web.exposure.include=health,metrics,prometheus
management.metrics.export.prometheus.enabled=true
```

Access metrics:
- http://localhost:8080/actuator/metrics
- http://localhost:8080/actuator/metrics/cache.gets
- http://localhost:8080/actuator/metrics/cache.puts

## 📚 Additional Resources

### Documentation
- [Spring Data Redis Reactive](https://docs.spring.io/spring-data/redis/docs/current/reference/html/#redis:reactive)
- [Lettuce Documentation](https://lettuce.io/core/release/reference/)
- [Redis Documentation](https://redis.io/documentation)

### Caching Patterns
- [Cache-Aside Pattern](https://docs.microsoft.com/en-us/azure/architecture/patterns/cache-aside)
- [Caching Best Practices](https://redis.io/topics/lru-cache)

### Tutorials
- [Spring Data Redis Reactive Tutorial](https://www.baeldung.com/spring-data-redis-reactive)
- [Redis Caching Strategies](https://redis.io/topics/cache)

## 🎓 Exercises

1. **Basic**: Add TTL (Time To Live) to cached entries
2. **Intermediate**: Implement cache invalidation on update and delete
3. **Advanced**: Implement cache warming on application startup
4. **Challenge**: Add cache hit/miss metrics and expose via Actuator

## 🤔 FAQ

**Q: When should I use Redis caching?**
A: Use caching for frequently read, infrequently written data. Good for session storage, API responses, database query results.

**Q: How do I handle cache and database inconsistency?**
A: Implement proper cache invalidation on writes, or use TTL to limit inconsistency window.

**Q: What's the difference between Lettuce and Jedis?**
A: Lettuce is fully reactive and thread-safe. Jedis is blocking. For reactive applications, always use Lettuce.

**Q: How do I prevent cache stampede?**
A: Use Reactor's `.cache()` operator or implement distributed locks with Redis.

**Q: Should I cache everything?**
A: No! Only cache data with high read-to-write ratio. Caching everything can increase complexity without benefits.

## 📝 Next Steps

After mastering this module, explore:
- **Distributed caching** with Redis Cluster
- **Cache eviction policies** (LRU, LFU, etc.)
- **Redis Pub/Sub** for cache invalidation across instances
- **Spring Cache Abstraction** with `@Cacheable`
- **Redis Streams** for event sourcing

---

**Happy Caching! ⚡**

