package training.courseswebclient;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class CourseController {

    public final EmployeesGateway employeesGateway;

    @GetMapping
    public Flux<Employee> findAll() {
        return employeesGateway.findAll();
    }
}
