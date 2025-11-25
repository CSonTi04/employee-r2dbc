# Employees Web - Spring WebFlux REST API

A Spring Boot reactive web application demonstrating REST API development with Spring WebFlux.

## 📋 Overview

This project showcases how to build reactive REST APIs using Spring WebFlux. It demonstrates reactive controllers, validation, error handling, and reactive service layers without database persistence (uses in-memory data).

## 🎯 Learning Objectives

After working with this project, you should understand:
- Building REST controllers with Spring WebFlux
- Returning `Mono` and `Flux` from endpoints
- Request validation with Bean Validation
- Error handling in reactive applications
- Reactive request/response patterns
- Testing reactive endpoints

## 🛠️ Technology Stack

- **Java 21** - Modern Java LTS version
- **Spring Boot 4.0.0** - Application framework
- **Spring WebFlux** - Reactive web framework
- **Lombok** - Reduce boilerplate code
- **Jakarta Validation** - Bean validation API
- **Spring Boot DevTools** - Hot reload during development
- **Maven** - Build and dependency management

## 📁 Project Structure

```
employees-web/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── employees/
│   │   │       ├── EmployeesWebApplication.java  # Main application
│   │   │       ├── EmployeeController.java       # REST endpoints
│   │   │       ├── EmployeeService.java          # Business logic
│   │   │       ├── EmployeeRepository.java       # Data access (in-memory)
│   │   │       ├── Employee.java                 # Entity
│   │   │       ├── EmployeeDto.java              # Data Transfer Object
│   │   │       ├── EmployeeErrorHandler.java     # Global error handler
│   │   │       ├── Violation.java                # Validation error DTO
│   │   │       ├── CounterController.java        # Counter example
│   │   │       ├── HelloController.java          # Hello world example
│   │   │       └── HelloMessage.java             # Hello message DTO
│   │   └── resources/
│   │       └── application.properties
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

### Build the Project

```bash
# Using Maven
mvn clean install

# Using Maven wrapper (Windows)
.\mvnw.cmd clean install

# Using Maven wrapper (Unix/Mac)
./mvnw clean install
```

### Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Using Maven wrapper (Windows)
.\mvnw.cmd spring-boot:run

# Using Maven wrapper (Unix/Mac)
./mvnw spring-boot:run

# Or run the JAR
java -jar target/employees-web-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

### Run Tests

```bash
mvn test
```

## 🌐 API Endpoints

### Hello World

```http
GET http://localhost:8080/hello
```

Returns a simple hello message.

### Counter

```http
GET http://localhost:8080/api/counter
```

Returns a reactive counter that emits values.

### Employee CRUD Operations

#### List All Employees

```http
GET http://localhost:8080/api/employees
```

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "name": "John Doe"
  },
  {
    "id": 2,
    "name": "Jane Doe"
  }
]
```

#### Get Employee by ID

```http
GET http://localhost:8080/api/employees/1
```

**Response**: `200 OK`
```json
{
  "id": 1,
  "name": "John Doe"
}
```

**Response**: `404 Not Found` (if employee doesn't exist)

#### Create Employee

```http
POST http://localhost:8080/api/employees
Content-Type: application/json

{
  "name": "Jack Doe"
}
```

**Response**: `201 Created`
```
Location: http://localhost:8080/api/employees/3
```
```json
{
  "id": 3,
  "name": "Jack Doe"
}
```

**Validation Error**: `400 Bad Request`
```json
{
  "violations": [
    {
      "field": "name",
      "message": "must not be blank"
    }
  ]
}
```

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
```json
{
  "id": 1,
  "name": "John Smith"
}
```

**Response**: `400 Bad Request` (if ID in path doesn't match ID in body)
**Response**: `404 Not Found` (if employee doesn't exist)

#### Delete Employee

```http
DELETE http://localhost:8080/api/employees/1
```

**Response**: `204 No Content`

## 💻 Code Examples

### REST Controller

```java
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @GetMapping
    public Flux<EmployeeDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<EmployeeDto>> findById(@PathVariable long id) {
        return service
                .findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<EmployeeDto>> save(
            @Valid @RequestBody Mono<EmployeeDto> employeeDto,
            UriComponentsBuilder uriComponentsBuilder) {
        return service
                .save(employeeDto)
                .map(e -> ResponseEntity
                    .created(uriComponentsBuilder
                        .path("/api/employees/{id}")
                        .buildAndExpand(e.id()).toUri())
                    .body(e));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<EmployeeDto>> update(
            @PathVariable long id,
            @Valid @RequestBody Mono<EmployeeDto> employeeDto) {
        return employeeDto
                .filter(e -> e.id() != null && e.id() == id)
                .switchIfEmpty(Mono.error(
                    new IllegalArgumentException("ID mismatch: %d".formatted(id))
                ))
                .flatMap(e -> service.save(Mono.just(e)))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable long id) {
        return service.deleteById(id);
    }
}
```

### Service Layer

```java
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;

    public Flux<EmployeeDto> findAll() {
        return repository.findAll().map(EmployeeService::toDto);
    }

    public Mono<EmployeeDto> findById(long id) {
        return repository.findById(id).map(EmployeeService::toDto);
    }

    public Mono<EmployeeDto> save(Mono<EmployeeDto> employeeDto) {
        return employeeDto
                .map(EmployeeService::toEntity)
                .flatMap(repository::save)
                .map(EmployeeService::toDto);
    }

    public Mono<Void> deleteById(long id) {
        return repository.deleteById(id);
    }

    private static EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(employee.getId(), employee.getName());
    }

    private static Employee toEntity(EmployeeDto employeeDto) {
        return new Employee(employeeDto.id(), employeeDto.name());
    }
}
```

### Data Transfer Object

```java
public record EmployeeDto(
    Long id,
    
    @NotBlank(message = "Name cannot be blank")
    String name
) {}
```

### Error Handler

```java
@RestControllerAdvice
public class EmployeeErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<Violation>> handleValidationError(
            MethodArgumentNotValidException e) {
        List<Violation> violations = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new Violation(err.getField(), err.getDefaultMessage()))
                .toList();
        return Map.of("violations", violations);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException e) {
        return Map.of("error", e.getMessage());
    }
}
```

## 🎯 Key Features

### 1. Reactive Controllers

Return `Mono<T>` or `Flux<T>` from controller methods:

```java
// Single item
@GetMapping("/{id}")
public Mono<EmployeeDto> findById(@PathVariable long id) {
    return service.findById(id);
}

// Multiple items
@GetMapping
public Flux<EmployeeDto> findAll() {
    return service.findAll();
}
```

### 2. Request Body Handling

Accept `Mono<T>` as request body for reactive processing:

```java
@PostMapping
public Mono<ResponseEntity<EmployeeDto>> save(
        @Valid @RequestBody Mono<EmployeeDto> employeeDto) {
    return service.save(employeeDto)
        .map(ResponseEntity::ok);
}
```

### 3. Validation

Use Jakarta Bean Validation annotations:

```java
public record EmployeeDto(
    Long id,
    
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 100)
    String name
) {}
```

### 4. Error Handling

Global error handling with `@RestControllerAdvice`:

```java
@RestControllerAdvice
public class EmployeeErrorHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<Violation>> handleValidationError(
            MethodArgumentNotValidException e) {
        // Handle validation errors
    }
}
```

### 5. Response Status

Set HTTP status codes:

```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public Mono<Void> deleteById(@PathVariable long id) {
    return service.deleteById(id);
}
```

### 6. Location Headers

Return `201 Created` with Location header:

```java
@PostMapping
public Mono<ResponseEntity<EmployeeDto>> save(
        @Valid @RequestBody Mono<EmployeeDto> employeeDto,
        UriComponentsBuilder uriComponentsBuilder) {
    return service.save(employeeDto)
        .map(e -> ResponseEntity
            .created(uriComponentsBuilder
                .path("/api/employees/{id}")
                .buildAndExpand(e.id()).toUri())
            .body(e));
}
```

## 🧪 Testing

### Running Tests

```bash
mvn test
```

### Testing with HTTP Files

Use the `employees.http` file in IntelliJ IDEA:

1. Open `employees.http`
2. Click the green arrow next to any request
3. View the response in the Run panel

### Manual Testing with curl

```bash
# List all employees
curl http://localhost:8080/api/employees

# Get employee by ID
curl http://localhost:8080/api/employees/1

# Create employee
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Jack Doe"}'

# Update employee
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"John Smith"}'

# Delete employee
curl -X DELETE http://localhost:8080/api/employees/1
```

## 📖 Common Patterns

### Pattern 1: Optional Response

```java
@GetMapping("/{id}")
public Mono<ResponseEntity<EmployeeDto>> findById(@PathVariable long id) {
    return service.findById(id)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
}
```

### Pattern 2: Conditional Processing

```java
@PutMapping("/{id}")
public Mono<ResponseEntity<EmployeeDto>> update(
        @PathVariable long id,
        @RequestBody Mono<EmployeeDto> employeeDto) {
    return employeeDto
        .filter(e -> e.id() != null && e.id() == id)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("ID mismatch")))
        .flatMap(service::save)
        .map(ResponseEntity::ok);
}
```

### Pattern 3: Reactive Composition

```java
public Mono<EmployeeDto> save(Mono<EmployeeDto> employeeDto) {
    return employeeDto
        .map(this::toEntity)
        .flatMap(repository::save)
        .map(this::toDto);
}
```

## 🐛 Common Pitfalls

### 1. Blocking in Controllers

```java
// ❌ WRONG - Blocking defeats reactive purpose
@GetMapping("/{id}")
public EmployeeDto findById(@PathVariable long id) {
    return service.findById(id).block();  // Don't block!
}

// ✅ CORRECT - Stay reactive
@GetMapping("/{id}")
public Mono<EmployeeDto> findById(@PathVariable long id) {
    return service.findById(id);
}
```

### 2. Not Handling Empty Results

```java
// ❌ WRONG - Returns 200 with empty body
@GetMapping("/{id}")
public Mono<EmployeeDto> findById(@PathVariable long id) {
    return service.findById(id);
}

// ✅ CORRECT - Returns 404 when not found
@GetMapping("/{id}")
public Mono<ResponseEntity<EmployeeDto>> findById(@PathVariable long id) {
    return service.findById(id)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
}
```

### 3. Incorrect Mono vs Flux Usage

```java
// ❌ WRONG - Using Flux for single result
@GetMapping("/{id}")
public Flux<EmployeeDto> findById(@PathVariable long id) {
    return service.findById(id).flux();
}

// ✅ CORRECT - Use Mono for single result
@GetMapping("/{id}")
public Mono<EmployeeDto> findById(@PathVariable long id) {
    return service.findById(id);
}
```

## ⚙️ Configuration

### application.properties

```properties
spring.application.name=employees-web

# Server port (default is 8080)
# server.port=8080

# Logging
# logging.level.org.springframework.web=DEBUG
# logging.level.employees=DEBUG
```

## 🔍 Debugging

### Enable Debug Logging

Add to `application.properties`:

```properties
logging.level.org.springframework.web=DEBUG
logging.level.reactor.netty=DEBUG
logging.level.employees=DEBUG
```

### Use Reactor Hooks

In your main class:

```java
@SpringBootApplication
public class EmployeesWebApplication {
    
    public static void main(String[] args) {
        Hooks.onOperatorDebug();  // Enable detailed stack traces
        SpringApplication.run(EmployeesWebApplication.class, args);
    }
}
```

### Add Logging to Reactive Chains

```java
public Mono<EmployeeDto> findById(long id) {
    return repository.findById(id)
        .log()  // Log all signals
        .map(this::toDto);
}
```

## 📚 Additional Resources

### Documentation
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Project Reactor](https://projectreactor.io/)

### Tutorials
- [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
- [Spring WebFlux Tutorial](https://www.baeldung.com/spring-webflux)

## 🎓 Exercises

1. **Basic**: Add a new endpoint to count all employees
2. **Intermediate**: Implement search by name with query parameters
3. **Advanced**: Add pagination to the list endpoint
4. **Challenge**: Implement partial updates with PATCH

## 🤔 FAQ

**Q: Why return Mono<ResponseEntity> instead of just Mono<T>?**
A: To have full control over HTTP status codes, especially for 404 Not Found scenarios.

**Q: When should I use @Valid with Mono<T>?**
A: Always validate reactive request bodies to ensure data integrity.

**Q: Can I use traditional Spring MVC annotations?**
A: Yes, most Spring MVC annotations work with WebFlux, but return reactive types.

**Q: How do I handle file uploads reactively?**
A: Use `Flux<FilePart>` with `@RequestPart` annotation.

## 📝 Next Steps

After mastering this module, proceed to:
- **employees-r2dbc**: Learn reactive database access with R2DBC
- **employees-r2dbc-immutable**: Advanced patterns with immutable entities

---

**Happy Coding! 🚀**

