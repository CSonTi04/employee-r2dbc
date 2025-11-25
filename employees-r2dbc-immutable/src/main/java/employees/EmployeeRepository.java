package employees;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {

    //Ez nem entitást ad vissza, hanem DTO-t
    Flux<EmployeeDto> findDtoAllBy();

    Mono<EmployeeDto> findDtoById(Long id);
}
