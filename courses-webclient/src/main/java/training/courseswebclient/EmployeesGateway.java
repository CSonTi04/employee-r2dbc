package training.courseswebclient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class EmployeesGateway {

    public Flux<Employee> findAll(){
        return WebClient.create("http://localhost:8080")
                .get()
                .uri("/api/employees")
                .retrieve()
                .bodyToFlux(Employee.class);
    }
}
