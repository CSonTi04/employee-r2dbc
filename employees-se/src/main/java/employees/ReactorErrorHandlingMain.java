package employees;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactorErrorHandlingMain {

    public static void main(String[] args) {
        Flux.just(new Employee("John Doe", 1970), new Employee("Jane Doe", 1980))
                .flatMap(e -> Flux.fromIterable(e.getFirstYears()))
                .subscribe(System.out::println);

        Flux.just(new Employee("John Doe", 1980), new Employee("Jane Doe", 1970))
                        .flatMap(e ->
                                Mono
                                        .just(e)
                                        .map(e1 -> e1.getAgeAt(1975))
                                        .doOnError(t -> t.printStackTrace())
                                        .onErrorResume(t -> Mono.empty())
                        )
                .subscribe(System.out::println);
        ;
    }
}
