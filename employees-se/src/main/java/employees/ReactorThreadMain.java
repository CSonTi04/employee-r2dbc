package employees;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class ReactorThreadMain {

    //https://www.jtechlog.hu/2024/02/01/project-reactor-szalkezeles.html
    public static void main(String[] args) {

        var pool = Schedulers.newParallel("parallel-scheduler");
        Flux.just(
                        new Employee("John", 1999),
                        new Employee("Jane", 1999),
                        new Employee("Doe", 1999)
                )
                .doOnNext(e -> System.out.println("Filtering: " + e + " on thread " + Thread.currentThread().getName()))
                .filter(emp -> emp.yearOfBirth() < 2000)
                .subscribeOn(pool)//Áttereli ide a szálakat
                //Publish on ws subscribe on
                //Publish hová tegye
                //Subscribe hol fusson?
                .doOnNext(e -> System.out.println("Mapping: " + e + " on thread " + Thread.currentThread().getName()))
                .map(Employee::name)
                //.subscribeOn(pool)
                .doOnNext(e -> System.out.println("uppercasing: " + e + " on thread " + Thread.currentThread().getName()))
                .map(String::toUpperCase)
                //.subscribeOn(pool)
                .subscribe(System.out::println);
    }
}
