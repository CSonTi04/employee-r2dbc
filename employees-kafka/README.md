# Employees R2DBC Immutable - Reactive with Immutable Entities

A full-stack reactive Spring Boot application demonstrating REST API development with Spring WebFlux, R2DBC database access, and **immutable entity patterns** using Java records.

## 📋 Overview

This project extends the `employees-r2dbc` example by emphasizing **immutability** and **functional programming** patterns in reactive applications. It demonstrates how to work with immutable entities (Java records) in the context of reactive database access.

## 🎯 Learning Objectives

After working with this project, you should understand:
- Using Java records as immutable entities
- Immutable patterns in reactive programming
- Functional programming style with reactive streams
- Benefits of immutability in concurrent applications
- Working with R2DBC and immutable data structures
- MapStruct for DTO mapping (planned enhancement)

## 🛠️ Technology Stack

- **Java 21** - Modern Java with records and pattern matching
- **Spring Boot 4.0.0** - Application framework
- **Spring WebFlux** - Reactive web framework
- **Spring Data R2DBC** - Reactive database access
- **R2DBC PostgreSQL** - Reactive PostgreSQL driver
- **r2dbc-migrate 3.3.0** - Database migration tool
- **Lombok** - Reduce boilerplate code (minimal use)
- **Jakarta Validation** - Bean validation API
- **PostgreSQL** - Relational database
- **Maven** - Build and dependency management

## 📁 Project Structure

```
employees-r2dbc-immutable/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── employees/
│   │   │       ├── EmployeesWebApplication.java  # Main application
│   │   │       ├── EmployeeController.java       # REST endpoints
│   │   │       ├── EmployeeService.java          # Business logic
│   │   │       ├── EmployeeRepository.java       # R2DBC repository
│   │   │       ├── Employee.java                 # Immutable entity (record)
│   │   │       ├── EmployeeDto.java              # Immutable DTO (record)
│   │   │       ├── EmployeeErrorHandler.java     # Global error handler
│   │   │       ├── Violation.java                # Validation error DTO
│   │   │       ├── CounterController.java        # Counter example
│   │   │       ├── HelloController.java          # Hello world example
│   │   │       └── HelloMessage.java             # Hello message DTO
│   │   └── resources/
│   │       ├── application.properties            # R2DBC configuration
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

## 🎨 Key Differences from employees-r2dbc

### 1. Immutable Entity (Record)

**employees-r2dbc** (Mutable):
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("employee")
public class Employee {
    @Id
    private Long id;
    private String name;
}
```

**employees-r2dbc-immutable** (Immutable):
```java
@Table("employee")
public record Employee(
    @Id Long id,
    String name
) {
    // Immutable by default - no setters!
}
```

### 2. Functional Service Layer

**employees-r2dbc** (Imperative style):
```java
public Mono<EmployeeDto> save(Mono<EmployeeDto> employeeDto) {
    return employeeDto
        .map(dto -> {
            Employee employee = new Employee();
            employee.setId(dto.id());
            employee.setName(dto.name());
            return employee;
        })
        .flatMap(repository::save)
        .map(this::toDto);
}
```

**employees-r2dbc-immutable** (Functional style):
```java
public Mono<EmployeeDto> save(Mono<EmployeeDto> employeeDto) {
    return employeeDto
        .map(EmployeeService::toEntity)  // Pure function
        .flatMap(repository::save)
        .map(EmployeeService::toDto);     // Pure function
}

private static Employee toEntity(EmployeeDto dto) {
    return new Employee(dto.id(), dto.name());
}

private static EmployeeDto toDto(Employee entity) {
    return new EmployeeDto(entity.id(), entity.name());
}
```

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8+ (or use included Maven wrapper)
- PostgreSQL 16+ (or Docker)

### Database Setup

#### Using Docker (Recommended)

```bash
docker run --name postgres-employees-immutable \
  -e POSTGRES_DB=employees \
  -e POSTGRES_USER=employees \
  -e POSTGRES_PASSWORD=employees \
  -p 5432:5432 \
  -d postgres:16
```

#### Using PowerShell (Windows)

```powershell
docker run --name postgres-employees-immutable `
  -e POSTGRES_DB=employees `
  -e POSTGRES_USER=employees `
  -e POSTGRES_PASSWORD=employees `
  -p 5432:5432 `
  -d postgres:16
```

### Build and Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Or using Maven wrapper
.\mvnw.cmd spring-boot:run
```

The application starts on **http://localhost:8080**

## 💻 Code Examples

### Immutable Entity (Java Record)

```java
package employees;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("employee")
public record Employee(
    @Id Long id,
    String name
) {
    // Records are immutable by default
    // - All fields are private final
    // - No setters
    // - Automatic equals(), hashCode(), toString()
    // - Canonical constructor
    
    // You can add custom methods if needed
    public Employee withName(String newName) {
        return new Employee(this.id, newName);
    }
    
    public Employee withId(Long newId) {
        return new Employee(newId, this.name);
    }
}
```

### Immutable DTO (Java Record)

```java
package employees;

import jakarta.validation.constraints.NotBlank;

public record EmployeeDto(
    Long id,
    
    @NotBlank(message = "Name cannot be blank")
    String name
) {
    // Immutable DTOs ensure data integrity
    // No risk of accidental modification
}
```

### Service Layer with Pure Functions

```java
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;

    public Flux<EmployeeDto> findAll() {
        return repository.findAll()
            .map(EmployeeService::toDto);
    }

    public Mono<EmployeeDto> findById(long id) {
        return repository.findById(id)
            .map(EmployeeService::toDto);
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

    // Pure functions - no side effects
    private static EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(employee.id(), employee.name());
    }

    private static Employee toEntity(EmployeeDto employeeDto) {
        return new Employee(employeeDto.id(), employeeDto.name());
    }
}
```

### Repository

```java
package employees;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeRepository 
        extends ReactiveCrudRepository<Employee, Long> {
    
    // Custom query methods
    Flux<Employee> findByNameContaining(String name);
    
    Mono<Employee> findByName(String name);
}
```

### Controller

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
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

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

## 🎯 Benefits of Immutability

### 1. Thread Safety

```java
// Immutable records are inherently thread-safe
public record Employee(Long id, String name) {}

// Multiple threads can safely share the same instance
Flux.range(1, 100)
    .parallel()
    .map(i -> employee)  // Safe to share
    .subscribe();
```

### 2. No Defensive Copying

```java
// With mutable objects
public Employee getEmployee() {
    return employee.clone();  // Need defensive copy
}

// With immutable records
public Employee getEmployee() {
    return employee;  // Safe to return directly
}
```

### 3. Predictable Behavior

```java
// With mutable objects
Employee employee = new Employee(1L, "John");
someMethod(employee);
// employee might be modified - unpredictable!

// With immutable records
Employee employee = new Employee(1L, "John");
someMethod(employee);
// employee is guaranteed unchanged
```

### 4. Easier Testing

```java
@Test
void testImmutableEmployee() {
    Employee employee = new Employee(1L, "John Doe");
    
    // No risk of test pollution
    service.process(employee);
    
    // employee is still the same
    assertEquals("John Doe", employee.name());
}
```

### 5. Better HashMap Performance

```java
// Records have reliable hashCode() and equals()
Map<Employee, String> map = new HashMap<>();
Employee key = new Employee(1L, "John");
map.put(key, "value");

// Works reliably with records
assertEquals("value", map.get(new Employee(1L, "John")));
```

## 🔄 Working with Immutable Data

### Creating Modified Copies

```java
// Option 1: With methods
public record Employee(Long id, String name) {
    public Employee withName(String newName) {
        return new Employee(this.id, newName);
    }
}

Employee updated = employee.withName("Jane Doe");

// Option 2: Direct construction
Employee employee = new Employee(1L, "John");
Employee updated = new Employee(employee.id(), "Jane Doe");

// Option 3: Using record patterns (Java 21+)
Employee updated = switch (employee) {
    case Employee(var id, var _) -> new Employee(id, "Jane Doe");
};
```

### Update Patterns in Service Layer

```java
public Mono<Employee> updateName(Long id, String newName) {
    return repository.findById(id)
        .map(employee -> new Employee(employee.id(), newName))
        .flatMap(repository::save);
}

// Or with helper method
public Mono<Employee> updateName(Long id, String newName) {
    return repository.findById(id)
        .map(employee -> employee.withName(newName))
        .flatMap(repository::save);
}
```

## ⚙️ Configuration

### application.properties

```properties
spring.application.name=employees-r2dbc-immutable

# R2DBC Configuration
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/employees
spring.r2dbc.username=employees
spring.r2dbc.password=employees

# Database Migration
r2dbc.migrate.resources=db/migration

# Enable SQL logging (optional)
# logging.level.io.r2dbc.postgresql.QUERY=DEBUG
```

### Database Schema

**V1__employees.sql**
```sql
CREATE TABLE IF NOT EXISTS employee (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
```

## 📖 Best Practices

### 1. Use Static Factory Methods

```java
public record Employee(Long id, String name) {
    
    // Factory method for new employees
    public static Employee create(String name) {
        return new Employee(null, name);
    }
    
    // Factory method with validation
    public static Employee of(Long id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        return new Employee(id, name);
    }
}
```

### 2. Use Method References

```java
// Prefer method references for pure functions
public Flux<EmployeeDto> findAll() {
    return repository.findAll()
        .map(EmployeeService::toDto);  // Method reference
}
```

### 3. Avoid Side Effects in Mapping

```java
// ❌ WRONG - Side effect in map
public Flux<Employee> findAll() {
    return repository.findAll()
        .map(e -> {
            logger.info("Found: " + e);  // Side effect!
            return e;
        });
}

// ✅ CORRECT - Use doOnNext for side effects
public Flux<Employee> findAll() {
    return repository.findAll()
        .doOnNext(e -> logger.info("Found: " + e))
        .map(EmployeeService::toDto);
}
```

### 4. Use Compact Constructor for Validation

```java
public record Employee(Long id, String name) {
    
    // Compact constructor for validation
    public Employee {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        // Normalize name
        name = name.trim();
    }
}
```

## 🧪 Testing

### Testing Immutable Entities

```java
@Test
void testImmutability() {
    Employee original = new Employee(1L, "John Doe");
    Employee modified = original.withName("Jane Doe");
    
    // Original is unchanged
    assertEquals("John Doe", original.name());
    assertEquals("Jane Doe", modified.name());
    assertNotSame(original, modified);
}

@Test
void testEquality() {
    Employee e1 = new Employee(1L, "John Doe");
    Employee e2 = new Employee(1L, "John Doe");
    
    // Records have value equality
    assertEquals(e1, e2);
    assertEquals(e1.hashCode(), e2.hashCode());
}
```

### Testing Service Layer

```java
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    
    @Mock
    private EmployeeRepository repository;
    
    @InjectMocks
    private EmployeeService service;
    
    @Test
    void testFindById() {
        Employee employee = new Employee(1L, "John Doe");
        when(repository.findById(1L)).thenReturn(Mono.just(employee));
        
        StepVerifier.create(service.findById(1L))
            .expectNextMatches(dto -> 
                dto.id() == 1L && dto.name().equals("John Doe")
            )
            .verifyComplete();
    }
}
```

## 🔍 Future Enhancements

### MapStruct Integration (Planned)

Replace manual mapping with MapStruct:

```java
// TODO: MapStruct kéne használni (as noted in code)
@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    
    EmployeeDto toDto(Employee employee);
    
    Employee toEntity(EmployeeDto dto);
    
    List<EmployeeDto> toDtoList(List<Employee> employees);
}
```

Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
```

## 🐛 Common Pitfalls

### 1. Trying to Modify Records

```java
// ❌ WRONG - Records are immutable
Employee employee = new Employee(1L, "John");
// employee.setName("Jane");  // Compilation error!

// ✅ CORRECT - Create new instance
Employee updated = new Employee(employee.id(), "Jane");
// Or use helper method
Employee updated = employee.withName("Jane");
```

### 2. Forgetting @Table Annotation

```java
// ❌ WRONG - Spring Data won't find the table
public record Employee(Long id, String name) {}

// ✅ CORRECT - Specify table name
@Table("employee")
public record Employee(Long id, String name) {}
```

### 3. Not Handling Null IDs

```java
// ❌ WRONG - NPE risk with auto-generated IDs
public Employee create(String name) {
    return new Employee(0L, name);  // ID should be null for new entities
}

// ✅ CORRECT - Use null for new entities
public Employee create(String name) {
    return new Employee(null, name);
}
```

## 📚 Additional Resources

### Java Records
- [JEP 395: Records](https://openjdk.org/jeps/395)
- [Java Records Tutorial](https://www.baeldung.com/java-record-keyword)

### Immutability
- [Effective Java - Item 17: Minimize Mutability](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [Benefits of Immutability](https://www.yegor256.com/2014/06/09/objects-should-be-immutable.html)

### Reactive Programming
- [Spring Data R2DBC](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/)
- [Project Reactor](https://projectreactor.io/)

## 🎓 Exercises

1. **Basic**: Add a `createdAt` timestamp field to the Employee record
2. **Intermediate**: Implement a `withUpdatedName` method that validates the name
3. **Advanced**: Create a composite record with nested immutable objects
4. **Challenge**: Implement MapStruct for automatic DTO mapping

## 🤔 FAQ

**Q: Can I use records with JPA/Hibernate?**
A: Yes, but with limitations. JPA requires a no-arg constructor which records don't have. Use R2DBC for better support.

**Q: How do I update a record in the database?**
A: Create a new record instance with updated values and save it. The ID remains the same.

**Q: Are records more performant than classes?**
A: Records have slightly better memory footprint and faster equals/hashCode, but the difference is usually negligible.

**Q: Can I add custom methods to records?**
A: Yes! You can add instance methods, static methods, and custom constructors.

**Q: Should all my entities be records?**
A: Use records for immutable data. If you need mutability for performance reasons, use classes.

## 📝 Next Steps

After mastering this module, explore:
- **Reactive Security** with Spring Security WebFlux
- **Event-driven architecture** with reactive messaging
- **GraphQL** with Spring GraphQL and reactive data sources
- **Kotlin** with coroutines as an alternative to reactive streams

---

**Happy Functional Reactive Coding! 🚀**

