package training.courseswebclient;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class CourseService {

    public Flux<CourseAttend> findAll() {
        return Flux.just(
                new CourseAttend("SPRING-BOOT", 1L),
                new CourseAttend("SPRING-BOOT", 2L),
                new CourseAttend("SPRING-BOOT", 3L),
                new CourseAttend("REACT", 2L),
                new CourseAttend("REACT", 3L),
                new CourseAttend("DOCKER", 1L)
        );
    }
}
