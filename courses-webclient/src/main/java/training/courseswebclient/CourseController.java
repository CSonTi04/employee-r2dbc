package training.courseswebclient;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CourseController {

    public final EmployeesClient employeesClient;

    private final CourseService courseService;

    @GetMapping("/employees")
    public Flux<Employee> findAll() {
        return employeesClient.findAll();
    }

    @GetMapping("/attendances")
    public Flux<CourseAttendDto> findAllCourseAttends() {
        return courseService.findAll()
                .flatMap(attend -> employeesClient.findById(attend.employeeId())
                        .map(emp -> new CourseAttendDto(
                                attend.courseCode(),
                                attend.employeeId(),
                                emp.name()
                        ))
                );
    }
}