# Employees SE - Standalone Reactor Examples

A pure Java SE application demonstrating Project Reactor fundamentals without Spring Framework dependencies.

## 📋 Overview

This project serves as an introduction to reactive programming using Project Reactor. It contains standalone Java applications that demonstrate core reactive concepts, operators, and error handling patterns without the complexity of a web framework.

## 🎯 Learning Objectives

After working with this project, you should understand:
- Difference between `Mono` and `Flux`
- Basic reactive operators: `map`, `filter`, `flatMap`
- Error handling in reactive streams
- Lazy evaluation and subscription
- Backpressure concepts
- When NOT to use `block()` methods

## 🛠️ Technology Stack

- **Java 21** - Modern Java with records and enhanced features
- **Project Reactor 3.8.0** - Reactive programming library
- **Reactor Test 3.8.0** - Testing utilities for reactive streams
- **JUnit 5.0.1** - Unit testing framework
- **Maven** - Build and dependency management

## 📁 Project Structure

```
employees-se/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── employees/
│   │           ├── Employee.java                  # Domain model (record)
│   │           ├── EmployeeService.java           # Business logic examples
│   │           ├── ReactorMain.java              # Basic Reactor demos
│   │           └── ReactorErrorHandlingMain.java # Error handling demos
│   └── test/
│       └── java/
│           └── employees/
└── pom.xml
```

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8+

### Build the Project

```bash
mvn clean compile
```

### Run Examples

#### Basic Reactor Examples

```bash
mvn exec:java -Dexec.mainClass="employees.ReactorMain"
```

This demonstrates:
- Creating `Flux` and `Mono` publishers
- Filtering and mapping operations
- Difference between `subscribe()` and `block()`
- Error handling with `onErrorReturn()`
- Working with reactive sequences

#### Error Handling Examples

```bash
mvn exec:java -Dexec.mainClass="employees.ReactorErrorHandlingMain"
```

This demonstrates:
- Different error handling strategies
- `onErrorReturn()` - provide default value
- `onErrorResume()` - switch to fallback publisher
- `doOnError()` - side effect on error
- Error recovery patterns

### Run Tests

```bash
mvn test
```

## 📚 Key Concepts Demonstrated

### 1. Mono vs Flux

```java
// Mono: 0 or 1 element
Mono<Employee> mono = Mono.just(new Employee("John", 1980));

// Flux: 0 to N elements
Flux<Employee> flux = Flux.just(
    new Employee("John", 1980), 
    new Employee("Jane", 1985)
);
```

### 2. Lazy Evaluation

Reactive streams are **lazy** - nothing happens until you `subscribe()`:

```java
Flux<Employee> flux = Flux.just(new Employee("John", 1980))
    .filter(e -> e.yearOfBirth() > 1975)
    .map(Employee::name);
// No execution yet!

flux.subscribe(System.out::println); // Now it executes
```

### 3. Operators

#### map() - Transform Elements

```java
Flux.just(new Employee("John", 1980))
    .map(Employee::name)  // Employee -> String
    .subscribe(System.out::println);
```

#### filter() - Select Elements

```java
Flux.just(new Employee("John", 1980), new Employee("Jane", 1985))
    .filter(e -> e.yearOfBirth() > 1982)
    .subscribe(System.out::println);
```

#### flatMap() - Async Transformation

```java
Flux.just(employee)
    .flatMap(e -> someAsyncOperation(e))
    .subscribe();
```

### 4. Error Handling

#### onErrorReturn() - Default Value

```java
Flux.just(new Employee("John Doe", 1970))
    .map(e -> e.getAgeAt(1960))  // Will throw exception
    .onErrorReturn(0)            // Return 0 on error
    .subscribe(System.out::println);
```

#### doOnError() - Side Effect

```java
Flux.just(employee)
    .map(e -> e.getAgeAt(1960))
    .doOnError(t -> t.printStackTrace())  // Log error
    .onErrorReturn(0)
    .subscribe(System.out::println);
```

### 5. The Block Anti-Pattern

**⚠️ IMPORTANT**: Never use `block()` in production reactive code!

```java
// ❌ WRONG - Defeats the purpose of reactive programming
String name = Flux.just(new Employee("Jane", 1985))
    .map(Employee::name)
    .blockFirst();  // Blocks the thread!

// ✅ CORRECT - Stay reactive
Flux.just(new Employee("Jane", 1985))
    .map(Employee::name)
    .subscribe(System.out::println);  // Non-blocking
```

**Why?**
- `block()` defeats the purpose of reactive programming
- It blocks the calling thread, losing all benefits
- In Spring WebFlux, `block()` throws exceptions to prevent misuse
- Use `subscribe()` or return the reactive type

### 6. Backpressure

Reactive streams implement backpressure - the subscriber controls the rate of data:

```java
Flux.range(1, 1000)
    .subscribe(new BaseSubscriber<Integer>() {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            request(10); // Request only 10 items
        }
        
        @Override
        protected void hookOnNext(Integer value) {
            System.out.println(value);
            request(1); // Request next item
        }
    });
```

## 💡 Code Examples

### Employee Record

```java
public record Employee(String name, int yearOfBirth) {
    
    public int getAgeAt(int year) {
        if (year < yearOfBirth) {
            throw new IllegalArgumentException(
                "Year %d cannot be before birth year %d"
                    .formatted(year, yearOfBirth)
            );
        }
        return year - yearOfBirth;
    }
    
    public List<Integer> getFirstYears() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            result.add(yearOfBirth + i);
        }
        return result;
    }
}
```

### ReactorMain Highlights

```java
public class ReactorMain {
    public static void main(String[] args) {
        // Creating a Flux
        Flux.just(new Employee("John", 1980), new Employee("Jane", 1985))
            .filter(employee -> employee.yearOfBirth() > 1982)
            .map(Employee::name)
            .subscribe(System.out::println);
        
        // Single element or empty
        Flux.just(new Employee("John", 1980), new Employee("Jane", 1985))
            .filter(employee -> employee.yearOfBirth() > 1982)
            .singleOrEmpty()
            .subscribe(System.out::println);
        
        // Error handling
        Flux.just(new Employee("John Doe", 1970))
            .map(e -> e.getAgeAt(1960))
            .doOnError(t -> t.printStackTrace())
            .onErrorReturn(0)
            .subscribe(System.out::println);
    }
}
```

## 🧪 Testing Reactive Code

Use `StepVerifier` from reactor-test:

```java
@Test
void testEmployeeFlux() {
    Flux<Employee> flux = Flux.just(
        new Employee("John", 1980),
        new Employee("Jane", 1985)
    );
    
    StepVerifier.create(flux)
        .expectNext(new Employee("John", 1980))
        .expectNext(new Employee("Jane", 1985))
        .verifyComplete();
}
```

## 📖 Common Patterns

### Pattern 1: Filter and Transform

```java
Flux<String> names = employeeFlux
    .filter(e -> e.yearOfBirth() > 1980)
    .map(Employee::name);
```

### Pattern 2: Error Recovery

```java
Flux<Integer> ages = employeeFlux
    .map(e -> e.getAgeAt(2025))
    .onErrorReturn(-1);
```

### Pattern 3: Conditional Logic

```java
Mono<Employee> result = employeeFlux
    .filter(e -> e.yearOfBirth() > 1982)
    .singleOrEmpty()
    .switchIfEmpty(Mono.just(new Employee("Default", 2000)));
```

## 🐛 Common Pitfalls

### 1. Forgetting to Subscribe

```java
// ❌ Nothing happens - no subscription
Flux.just(1, 2, 3).map(x -> x * 2);

// ✅ Subscribe to trigger execution
Flux.just(1, 2, 3).map(x -> x * 2).subscribe();
```

### 2. Using Block in Reactive Code

```java
// ❌ Don't block in reactive pipelines
flux.map(e -> someFlux.blockFirst());

// ✅ Use flatMap for composition
flux.flatMap(e -> someFlux);
```

### 3. Not Handling Errors

```java
// ❌ Unhandled error terminates stream
Flux.just(employee).map(e -> e.getAgeAt(1900));

// ✅ Handle errors
Flux.just(employee)
    .map(e -> e.getAgeAt(1900))
    .onErrorReturn(0);
```

## 🔍 Debugging Tips

### Enable Operator Debug Mode

```java
Hooks.onOperatorDebug();
```

### Use log() Operator

```java
Flux.just(1, 2, 3)
    .log()  // Logs all reactive signals
    .map(x -> x * 2)
    .subscribe();
```

### Use doOn* Operators

```java
Flux.just(employee)
    .doOnNext(e -> System.out.println("Processing: " + e))
    .doOnError(e -> System.err.println("Error: " + e))
    .doOnComplete(() -> System.out.println("Done!"))
    .subscribe();
```

## 📚 Additional Resources

### Documentation
- [Project Reactor Reference](https://projectreactor.io/docs/core/release/reference/)
- [Reactor Core Javadoc](https://projectreactor.io/docs/core/release/api/)

### Tutorials
- [Reactor Getting Started](https://projectreactor.io/docs/core/release/reference/#getting-started)
- [Lite Rx API Hands-On](https://github.com/reactor/lite-rx-api-hands-on)

### Videos
- [Flight of the Flux](https://www.youtube.com/watch?v=Cj4foJzPF80)
- [Reactive Programming with Project Reactor](https://www.youtube.com/results?search_query=project+reactor+tutorial)

## 🎓 Exercises

1. **Basic**: Create a Flux of 10 employees and filter those born after 1990
2. **Intermediate**: Chain multiple operators (filter, map, flatMap) together
3. **Advanced**: Implement custom error handling with retry logic
4. **Challenge**: Create a reactive pipeline with backpressure handling

## 🤔 FAQ

**Q: When should I use Mono vs Flux?**
A: Use `Mono<T>` when you expect 0 or 1 result (like finding by ID). Use `Flux<T>` when you expect 0 to many results (like listing all).

**Q: Why does nothing happen when I create a Flux?**
A: Reactive streams are lazy. You must call `subscribe()` to start execution.

**Q: Can I use block() for testing?**
A: In tests, `block()` is acceptable. In production code, never use it. Use `StepVerifier` for proper reactive testing.

**Q: How do I debug reactive code?**
A: Use `.log()` operator, `Hooks.onOperatorDebug()`, or `doOn*` operators for debugging.

## 📝 Next Steps

After mastering this module, proceed to:
- **employees-web**: Learn Spring WebFlux REST APIs
- **employees-r2dbc**: Reactive database access with R2DBC

---

**Happy Learning! 🚀**

