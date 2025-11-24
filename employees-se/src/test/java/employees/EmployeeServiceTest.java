package employees;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    @Test
    void findEmployeeByName() {
        EmployeeService employeeService = new EmployeeService();
        StepVerifier.create(employeeService.findEmployeeByName("John Doe"))
//                .expectNext(new Employee("John Doe", 1970))
                .expectNextMatches(e -> e.name().equals("John Doe"))
                .verifyComplete();
    }
}