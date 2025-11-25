package employees;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class TimeoutDemoMain {

    public static void main(String[] args) {
        Mono.fromCallable(TimeoutDemoMain::blockingMethod)
                .subscribeOn(Schedulers.newParallel("blocking-scheduler"))
                .timeout(Duration.ofSeconds(2))
                //Interruptot is hív, ha timeout van
                .onErrorResume(TimeoutException.class, t -> Mono.just(-1))
                .subscribe(System.out::println);
    }

    public static int blockingMethod() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Blocking method finished");
        return 110;
    }
}
