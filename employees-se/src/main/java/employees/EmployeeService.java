package employees;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class EmployeeService {

    private List<Employee> employees = List.of(new Employee("John Doe", 1970), new Employee("Jane Doe", 1980));

    public Mono<Employee> findEmployeeByName(String name) {
        return Flux.fromIterable(employees)
                .filter(e -> e.name().equals(name))
                .singleOrEmpty();
    }
}
