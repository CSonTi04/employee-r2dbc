    # Employees R2DBC - Reactive Database Access

A full-stack reactive Spring Boot application demonstrating REST API development with Spring WebFlux and reactive database access using R2DBC (Reactive Relational Database Connectivity).

## 📋 Overview

This project showcases a complete reactive stack from web layer to database. It uses R2DBC for non-blocking, reactive database operations with PostgreSQL, demonstrating how to build truly end-to-end reactive applications.

## 🎯 Learning Objectives

After working with this project, you should understand:
- Reactive database access with R2DBC
- Configuring R2DBC data sources
- Creating reactive repositories
- Database migrations with r2dbc-migrate
- Full-stack reactive architecture
- Integration testing with reactive databases
- Performance benefits of reactive I/O

## 🛠️ Technology Stack

- **Java 21** - Modern Java LTS version
- **Spring Boot 4.0.0** - Application framework
- **Spring WebFlux** - Reactive web framework
- **Spring Data R2DBC** - Reactive database access
- **R2DBC PostgreSQL** - Reactive PostgreSQL driver
- **r2dbc-migrate 3.3.0** - Database migration tool
- **Lombok** - Reduce boilerplate code
- **Jakarta Validation** - Bean validation API
- **PostgreSQL** - Relational database
- **Maven** - Build and dependency management

## 📁 Project Structure

```
employees-r2dbc/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── employees/
│   │   │       ├── EmployeesWebApplication.java  # Main application
│   │   │       ├── EmployeeController.java       # REST endpoints
│   │   │       ├── EmployeeService.java          # Business logic
│   │   │       ├── EmployeeRepository.java       # R2DBC repository
│   │   │       ├── Employee.java                 # Entity
│   │   │       ├── EmployeeDto.java              # Data Transfer Object
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

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8+ (or use included Maven wrapper)
- PostgreSQL 16+ (or Docker)

### Database Setup

#### Option 1: Using Docker (Recommended)

```bash
docker run --name postgres-employees \
  -e POSTGRES_DB=employees \
  -e POSTGRES_USER=employees \
  -e POSTGRES_PASSWORD=employees \
  -p 5432:5432 \
  -d postgres:16
```

#### Option 2: Using PowerShell (Windows)

```powershell
docker run --name postgres-employees `
  -e POSTGRES_DB=employees `
  -e POSTGRES_USER=employees `
  -e POSTGRES_PASSWORD=employees `
  -p 5432:5432 `
  -d postgres:16
```

#### Option 3: Local PostgreSQL Installation

Install PostgreSQL and run:

```sql
CREATE DATABASE employees;
CREATE USER employees WITH PASSWORD 'employees';
GRANT ALL PRIVILEGES ON DATABASE employees TO employees;
```

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
```

The application will:
1. Start on **http://localhost:8080**
2. Connect to PostgreSQL database
3. Run database migrations automatically
4. Create the `employee` table

### Verify Database Migration

```bash
# Connect to PostgreSQL
docker exec -it postgres-employees psql -U employees -d employees

# Check table
\dt

# Query table
SELECT * FROM employee;

# Exit
\q
```

## 🌐 API Endpoints

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

#### Delete Employee

```http
DELETE http://localhost:8080/api/employees/1
```

**Response**: `204 No Content`

## 💻 Code Examples

### R2DBC Repository

```java
public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {
    
    // Basic CRUD operations inherited from ReactiveCrudRepository:
    // - Mono<Employee> findById(Long id)
    // - Flux<Employee> findAll()
    // - Mono<Employee> save(Employee employee)
    // - Mono<Void> deleteById(Long id)
    
    // Custom query methods
    Flux<Employee> findByNameContaining(String name);
    
    Mono<Employee> findByName(String name);
}
```

### Entity

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

### Service Layer

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

    private static EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(employee.getId(), employee.getName());
    }

    private static Employee toEntity(EmployeeDto employeeDto) {
        Employee employee = new Employee();
        employee.setId(employeeDto.id());
        employee.setName(employeeDto.name());
        return employee;
    }
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

## ⚙️ Configuration

### application.properties

```properties
spring.application.name=employees-r2dbc

# R2DBC Configuration
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/employees
spring.r2dbc.username=employees
spring.r2dbc.password=employees

# Database Migration
r2dbc.migrate.resources=db/migration

# Optional: Enable SQL logging
# logging.level.io.r2dbc.postgresql.QUERY=DEBUG
# logging.level.io.r2dbc.postgresql.PARAM=DEBUG
```

### Database Migration

Create migration files in `src/main/resources/db/migration/`:

**V1__employees.sql**
```sql
CREATE TABLE IF NOT EXISTS employee (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
```

**V2__add_sample_data.sql** (example)
```sql
INSERT INTO employee (name) VALUES ('John Doe');
INSERT INTO employee (name) VALUES ('Jane Doe');
```

Migration files follow the naming pattern: `V{version}__{description}.sql`

## 🎯 Key Features

### 1. Reactive CRUD Repository

R2DBC provides reactive repositories out of the box:

```java
public interface EmployeeRepository 
        extends ReactiveCrudRepository<Employee, Long> {
    // All CRUD methods return Mono or Flux
}
```

### 2. Custom Query Methods

Spring Data R2DBC supports query derivation:

```java
public interface EmployeeRepository 
        extends ReactiveCrudRepository<Employee, Long> {
    
    // Derived query methods
    Flux<Employee> findByNameContaining(String name);
    
    Mono<Employee> findByName(String name);
    
    Flux<Employee> findByNameStartingWith(String prefix);
}
```

### 3. Custom Queries with @Query

```java
public interface EmployeeRepository 
        extends ReactiveCrudRepository<Employee, Long> {
    
    @Query("SELECT * FROM employee WHERE name ILIKE :name")
    Flux<Employee> searchByName(@Param("name") String name);
    
    @Query("SELECT COUNT(*) FROM employee")
    Mono<Long> countAllEmployees();
}
```

### 4. Database Migrations

Automatic schema migration using r2dbc-migrate:

- Place SQL files in `src/main/resources/db/migration/`
- Migrations run automatically on application startup
- Version control your database schema
- Supports forward migrations

### 5. Transaction Support

R2DBC supports declarative transactions:

```java
@Service
public class EmployeeService {
    
    @Transactional
    public Mono<Employee> saveWithRelations(Employee employee) {
        return repository.save(employee)
            .flatMap(e -> relatedRepository.save(related));
    }
}
```

## 🧪 Testing

### Unit Testing Service Layer

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
            .expectNextMatches(dto -> dto.name().equals("John Doe"))
            .verifyComplete();
    }
}
```

### Integration Testing with Test Containers

```java
@SpringBootTest
@Testcontainers
class EmployeeRepositoryTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("employees")
        .withUsername("test")
        .withPassword("test");
    
    @Autowired
    private EmployeeRepository repository;
    
    @Test
    void testSaveAndFind() {
        Employee employee = new Employee(null, "Test Employee");
        
        StepVerifier.create(
            repository.save(employee)
                .flatMap(e -> repository.findById(e.getId()))
        )
        .expectNextMatches(e -> e.getName().equals("Test Employee"))
        .verifyComplete();
    }
}
```

### Testing Controllers

```java
@WebFluxTest(EmployeeController.class)
class EmployeeControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private EmployeeService service;
    
    @Test
    void testFindAll() {
        EmployeeDto dto = new EmployeeDto(1L, "John Doe");
        when(service.findAll()).thenReturn(Flux.just(dto));
        
        webTestClient.get()
            .uri("/api/employees")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(EmployeeDto.class)
            .hasSize(1)
            .contains(dto);
    }
}
```

## 📖 Common Patterns

### Pattern 1: Save and Return

```java
public Mono<EmployeeDto> save(EmployeeDto dto) {
    return Mono.just(dto)
        .map(this::toEntity)
        .flatMap(repository::save)
        .map(this::toDto);
}
```

### Pattern 2: Find or Create

```java
public Mono<Employee> findOrCreate(String name) {
    return repository.findByName(name)
        .switchIfEmpty(
            repository.save(new Employee(null, name))
        );
}
```

### Pattern 3: Batch Operations

```java
public Flux<Employee> saveAll(List<Employee> employees) {
    return repository.saveAll(employees);
}
```

### Pattern 4: Conditional Update

```java
public Mono<Employee> updateIfExists(Long id, Employee updated) {
    return repository.findById(id)
        .flatMap(existing -> {
            existing.setName(updated.getName());
            return repository.save(existing);
        });
}
```

## 🐛 Common Pitfalls

### 1. Forgetting Transactions

```java
// ❌ WRONG - Multiple saves may not be atomic
public Mono<Void> saveMultiple(Employee e1, Employee e2) {
    return repository.save(e1)
        .then(repository.save(e2))
        .then();
}

// ✅ CORRECT - Wrap in transaction
@Transactional
public Mono<Void> saveMultiple(Employee e1, Employee e2) {
    return repository.save(e1)
        .then(repository.save(e2))
        .then();
}
```

### 2. Blocking in Reactive Chain

```java
// ❌ WRONG - Blocking defeats reactive purpose
public Mono<Employee> save(Employee employee) {
    Employee saved = repository.save(employee).block();
    return Mono.just(saved);
}

// ✅ CORRECT - Stay reactive
public Mono<Employee> save(Employee employee) {
    return repository.save(employee);
}
```

### 3. Not Handling Empty Results

```java
// ❌ WRONG - No handling for not found
public Mono<Employee> update(Long id, Employee employee) {
    return repository.findById(id)
        .map(existing -> {
            existing.setName(employee.getName());
            return existing;
        })
        .flatMap(repository::save);
}

// ✅ CORRECT - Handle empty case
public Mono<Employee> update(Long id, Employee employee) {
    return repository.findById(id)
        .switchIfEmpty(Mono.error(
            new EntityNotFoundException("Employee not found: " + id)
        ))
        .map(existing -> {
            existing.setName(employee.getName());
            return existing;
        })
        .flatMap(repository::save);
}
```

## 🔍 Debugging

### Enable R2DBC Query Logging

Add to `application.properties`:

```properties
# Enable R2DBC SQL logging
logging.level.io.r2dbc.postgresql.QUERY=DEBUG
logging.level.io.r2dbc.postgresql.PARAM=DEBUG

# Spring Data R2DBC
logging.level.org.springframework.data.r2dbc=DEBUG

# General debugging
logging.level.employees=DEBUG
```

### Monitor Database Connections

```bash
# Connect to PostgreSQL
docker exec -it postgres-employees psql -U employees -d employees

# Check active connections
SELECT * FROM pg_stat_activity WHERE datname = 'employees';

# Check table data
SELECT * FROM employee;
```

### Use R2DBC Proxy for Debugging

Add dependency to `pom.xml`:

```xml
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-proxy</artifactId>
</dependency>
```

## 🚀 Performance Tips

### 1. Use Batch Operations

```java
// Save multiple employees efficiently
Flux<Employee> employees = Flux.just(
    new Employee(null, "John"),
    new Employee(null, "Jane")
);

repository.saveAll(employees)
    .subscribe();
```

### 2. Limit Result Sets

```java
Flux<Employee> employees = repository.findAll()
    .take(10)  // Limit to 10 results
    .subscribe();
```

### 3. Use Backpressure

```java
repository.findAll()
    .limitRate(100)  // Request 100 at a time
    .subscribe();
```

## 📚 Additional Resources

### Documentation
- [Spring Data R2DBC Reference](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/)
- [R2DBC Specification](https://r2dbc.io/spec/1.0.0.RELEASE/spec/html/)
- [R2DBC PostgreSQL Driver](https://github.com/pgjdbc/r2dbc-postgresql)
- [r2dbc-migrate](https://github.com/nkonev/r2dbc-migrate)

### Tutorials
- [Spring Data R2DBC Tutorial](https://www.baeldung.com/spring-data-r2dbc)
- [Reactive Database Access](https://spring.io/guides/gs/accessing-data-r2dbc/)

## 🎓 Exercises

1. **Basic**: Add a custom query method to find employees by name
2. **Intermediate**: Implement pagination for the list endpoint
3. **Advanced**: Add a second entity with a relationship to Employee
4. **Challenge**: Implement full-text search using PostgreSQL

## 🤔 FAQ

**Q: What's the difference between JPA and R2DBC?**
A: JPA is blocking (JDBC-based), R2DBC is non-blocking and reactive. R2DBC provides better scalability under high load.

**Q: Can I use JPA and R2DBC together?**
A: Yes, but be careful not to mix blocking and non-blocking code. Keep them in separate transactions.

**Q: Does R2DBC support all SQL databases?**
A: R2DBC has drivers for PostgreSQL, MySQL, MSSQL, Oracle, H2. Check [r2dbc.io](https://r2dbc.io) for current list.

**Q: How do I handle database migrations?**
A: Use r2dbc-migrate (reactive) or Flyway/Liquibase (run at startup, before reactive context).

**Q: Is R2DBC production-ready?**
A: Yes, R2DBC 1.0 was released in 2020 and is used in production by many companies.

## 📝 Next Steps

After mastering this module, proceed to:
- **employees-r2dbc-immutable**: Learn immutable entity patterns
- Explore reactive security with Spring Security WebFlux
- Add Redis caching with reactive repositories
- Implement WebSocket endpoints

---

**Happy Reactive Coding! 🚀**

