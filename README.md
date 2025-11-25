# Java Reactive Programming Training - 2025

This repository contains training materials and sample projects demonstrating reactive programming in Java using Project Reactor and Spring WebFlux.

## 📋 Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Projects](#projects)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Repository Structure](#repository-structure)
- [Learning Path](#learning-path)
- [Contributing](#contributing)

## 🎯 Overview

This training repository demonstrates the evolution of reactive programming in Java, from standalone Reactor examples to full-fledged Spring WebFlux applications with reactive database access using R2DBC.

The projects showcase:
- **Reactive Streams** - Understanding reactive programming fundamentals
- **Project Reactor** - Working with Mono and Flux publishers
- **Spring WebFlux** - Building reactive REST APIs
- **R2DBC** - Reactive database access with PostgreSQL
- **Error Handling** - Reactive error handling patterns
- **Backpressure** - Managing data flow in reactive streams

## 📚 Prerequisites

Before working with these projects, ensure you have the following installed:

- **Java 21** or higher
- **Maven 3.8+** for dependency management and building
- **PostgreSQL 16+** (for R2DBC projects)
- **IDE** - IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Docker** (optional, for running PostgreSQL)

### PostgreSQL Setup

For the R2DBC projects, you'll need a PostgreSQL database:

```bash
# Using Docker
docker run --name postgres-reactive \
  -e POSTGRES_DB=employees \
  -e POSTGRES_USER=employees \
  -e POSTGRES_PASSWORD=employees \
  -p 5432:5432 \
  -d postgres:16
```

Or install PostgreSQL directly and create the database:

```sql
CREATE DATABASE employees;
CREATE USER employees WITH PASSWORD 'employees';
GRANT ALL PRIVILEGES ON DATABASE employees TO employees;
```

## 🚀 Projects

### 1. [employees-se](./employees-se)
**Standalone Reactor Examples**

A pure Java SE application demonstrating Project Reactor fundamentals without Spring Framework.

- **Purpose**: Learn Reactor basics (Mono, Flux, operators)
- **Key Features**: Error handling, filtering, mapping
- **Tech Stack**: Java 21, Project Reactor Core
- **Entry Point**: `ReactorMain.java`, `ReactorErrorHandlingMain.java`

### 2. [employees-web](./employees-web)
**Spring WebFlux REST API**

A Spring Boot application with reactive REST endpoints using WebFlux.

- **Purpose**: Build reactive web applications
- **Key Features**: REST controllers, validation, error handling
- **Tech Stack**: Spring Boot 4.0, Spring WebFlux, Lombok
- **Port**: 8080
- **Database**: In-memory (mutable data)

### 3. [employees-r2dbc](./employees-r2dbc)
**Spring WebFlux with R2DBC**

Full reactive stack with Spring WebFlux and R2DBC for PostgreSQL.

- **Purpose**: Reactive database access with R2DBC
- **Key Features**: CRUD operations, database migrations, reactive repositories
- **Tech Stack**: Spring Boot 4.0, Spring WebFlux, R2DBC PostgreSQL, r2dbc-migrate
- **Port**: 8080
- **Database**: PostgreSQL (reactive)

### 4. [employees-r2dbc-immutable](./employees-r2dbc-immutable)
**Immutable Entities with R2DBC**

Similar to employees-r2dbc but emphasizes immutability patterns.

- **Purpose**: Demonstrate immutable entity patterns in reactive contexts
- **Key Features**: Immutable records, functional programming style
- **Tech Stack**: Spring Boot 4.0, Spring WebFlux, R2DBC PostgreSQL
- **Port**: 8080
- **Database**: PostgreSQL (reactive)

## 🛠️ Technology Stack

### Core Technologies
- **Java 21** - Latest LTS with modern language features
- **Project Reactor 3.8.0** - Reactive programming library
- **Spring Boot 4.0.0** - Application framework
- **Spring WebFlux** - Reactive web framework
- **R2DBC** - Reactive Relational Database Connectivity

### Supporting Libraries
- **Lombok** - Reduce boilerplate code
- **Jakarta Validation** - Bean validation
- **r2dbc-migrate** - Database migration tool for R2DBC
- **PostgreSQL R2DBC Driver** - Reactive PostgreSQL driver
- **JUnit 5** - Testing framework
- **Reactor Test** - Testing reactive streams

## 🏃 Getting Started

### Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd javax-react-training-2025-11-24
   ```

2. **Start with Reactor basics**
   ```bash
   cd employees-se
   mvn clean compile
   mvn exec:java -Dexec.mainClass="employees.ReactorMain"
   ```

3. **Run a Spring WebFlux application**
   ```bash
   cd ../employees-web
   mvn spring-boot:run
   ```

4. **Test the API**
   ```bash
   curl http://localhost:8080/api/employees
   ```

5. **Run R2DBC application** (requires PostgreSQL)
   ```bash
   cd ../employees-r2dbc
   mvn spring-boot:run
   ```

### Building All Projects

```bash
# Build all projects from root
for dir in employees-se employees-web employees-r2dbc employees-r2dbc-immutable; do
  cd $dir
  mvn clean install
  cd ..
done
```

### Running Tests

```bash
# Run tests for a specific project
cd employees-web
mvn test

# Or use Maven wrapper
./mvnw test
```

## 📁 Repository Structure

```
javax-react-training-2025-11-24/
├── employees-se/                    # Standalone Reactor examples
│   ├── src/main/java/employees/
│   │   ├── Employee.java           # Domain model
│   │   ├── EmployeeService.java    # Business logic
│   │   ├── ReactorMain.java        # Basic Reactor examples
│   │   └── ReactorErrorHandlingMain.java
│   └── pom.xml
│
├── employees-web/                   # Spring WebFlux application
│   ├── src/main/java/employees/
│   │   ├── EmployeesWebApplication.java
│   │   ├── EmployeeController.java # REST endpoints
│   │   ├── EmployeeService.java    # Business logic
│   │   ├── EmployeeRepository.java # Data access
│   │   ├── EmployeeDto.java        # Data transfer object
│   │   └── EmployeeErrorHandler.java
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── employees.http              # HTTP request examples
│   └── pom.xml
│
├── employees-r2dbc/                 # Spring WebFlux + R2DBC
│   ├── src/main/java/employees/
│   │   ├── EmployeesWebApplication.java
│   │   ├── EmployeeController.java
│   │   ├── EmployeeService.java
│   │   ├── EmployeeRepository.java # R2DBC repository
│   │   ├── Employee.java           # Entity
│   │   └── EmployeeDto.java
│   ├── src/main/resources/
│   │   ├── application.properties  # R2DBC configuration
│   │   └── db/migration/
│   │       └── V1__employees.sql   # Database schema
│   ├── employees.http
│   └── pom.xml
│
└── employees-r2dbc-immutable/       # Immutable R2DBC version
    ├── src/main/java/employees/
    ├── src/main/resources/
    └── pom.xml
```

## 📖 Learning Path

### Level 1: Reactor Fundamentals (employees-se)
1. Understand `Mono` and `Flux`
2. Learn basic operators: `map`, `filter`, `flatMap`
3. Error handling: `onErrorReturn`, `onErrorResume`, `doOnError`
4. Understand backpressure concepts

**Start with**: `ReactorMain.java` and `ReactorErrorHandlingMain.java`

### Level 2: Reactive Web (employees-web)
1. Build REST controllers with `@RestController`
2. Return `Mono` and `Flux` from endpoints
3. Handle validation with `@Valid`
4. Implement error handlers
5. Work with reactive request/response

**Start with**: `EmployeeController.java`

### Level 3: Reactive Database (employees-r2dbc)
1. Configure R2DBC data source
2. Use reactive repositories
3. Perform CRUD operations reactively
4. Handle database migrations
5. Test reactive database operations

**Start with**: `EmployeeRepository.java` and `EmployeeService.java`

### Level 4: Advanced Patterns (employees-r2dbc-immutable)
1. Work with immutable entities
2. Use Java records for DTOs
3. Functional programming style
4. Advanced reactive patterns

## 📝 Key Concepts

### Reactive Streams
- **Publisher**: Produces data (Mono, Flux)
- **Subscriber**: Consumes data
- **Subscription**: Links Publisher and Subscriber
- **Backpressure**: Subscriber controls data flow

### Mono vs Flux
- **Mono<T>**: 0 or 1 element
- **Flux<T>**: 0 to N elements

### Common Operators
- **map()**: Transform elements
- **filter()**: Select elements
- **flatMap()**: Async transformation
- **switchIfEmpty()**: Provide alternative
- **onErrorReturn()**: Return default on error
- **subscribe()**: Start the reactive flow

### Important Rules
- **Never block** in reactive pipelines
- Avoid `block()`, `blockFirst()`, `blockLast()` in production
- Use reactive operators instead of imperative code
- Handle errors reactively

## 🧪 Testing

Each project includes test examples:

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=EmployeesWebApplicationTests

# Run with coverage
mvn clean verify
```

## 🔧 Development

### Hot Reload
Projects use Spring Boot DevTools for hot reload during development:

```bash
mvn spring-boot:run
```

### API Testing
Use the provided `.http` files in IntelliJ IDEA or import into Postman/Insomnia.

### Debugging Reactive Code
- Use `log()` operator to see events
- Use `doOnNext()`, `doOnError()` for side effects
- Enable reactor debug mode: `Hooks.onOperatorDebug()`

## 🤝 Contributing

This is a training repository. Feel free to:
- Add more examples
- Improve documentation
- Fix bugs
- Add tests
- Share learning resources

## 📚 Additional Resources

### Official Documentation
- [Project Reactor Documentation](https://projectreactor.io/docs)
- [Spring WebFlux Guide](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [R2DBC Documentation](https://r2dbc.io/)

### Learning Materials
- [Reactor 3 Reference Guide](https://projectreactor.io/docs/core/release/reference/)
- [Reactive Programming with Spring Boot](https://spring.io/reactive)
- [Understanding Backpressure](https://projectreactor.io/docs/core/release/reference/#_on_backpressure_and_ways_to_reshape_requests)

### Books
- "Hands-On Reactive Programming in Spring 5" by Oleh Dokuka
- "Reactive Programming with RxJava" by Tomasz Nurkiewicz

## 📄 License

This project is for educational purposes.

## 👥 Authors

Training360 - Java and React Training Course 2025

---

**Happy Reactive Coding! 🚀**

