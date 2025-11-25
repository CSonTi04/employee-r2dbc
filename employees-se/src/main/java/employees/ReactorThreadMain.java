package employees;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class ReactorThreadMain {

    //https://www.jtechlog.hu/2024/02/01/project-reactor-szalkezeles.html
    public static void main(String[] args) {
        Flux.just(
                        new Employee("John", 1999),
                        new Employee("Jane", 1999),
                        new Employee("Doe", 1999)
                )
                .doOnNext(e -> System.out.println("Filtering: " + e + " on thread " + Thread.currentThread().getName()))
                .filter(emp -> emp.yearOfBirth() < 2000)
                .publishOn(Schedulers.newParallel("parallel-scheduler"))
                .doOnNext(e -> System.out.println("Mapping: " + e + " on thread " + Thread.currentThread().getName()))
                .map(Employee::name)
                .doOnNext(e -> System.out.println("uppercasing: " + e + " on thread " + Thread.currentThread().getName()))
                .map(String::toUpperCase)
                .subscribe(System.out::println);
    }
}
