package employees;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;
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

    //Itt timeout lesz, mert a metódus túl sokáig fut
    //De tovább fut a hívás a háttérben
    public static int blockingMethod() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            try {
                Random random = SecureRandom.getInstanceStrong();
                random.nextBytes(new byte[1024 * 1024]);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return 110;
    }
}
