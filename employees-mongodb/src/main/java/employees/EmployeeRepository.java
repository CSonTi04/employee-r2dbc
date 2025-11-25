package employees;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {

    Flux<EmployeeDto> findAllBy();

    <T> Mono<T> findDtoById(String id, Class<T> clazz);

    //Itt lehet SPEL-t használni paraméterezéshez
    @Query("""
            { 'name': ?0, 'id': { $ne: null } }
    """)
    Flux<EmployeeDto> findByName(String name);

}
