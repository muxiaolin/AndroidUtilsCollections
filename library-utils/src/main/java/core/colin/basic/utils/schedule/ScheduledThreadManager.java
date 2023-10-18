package core.colin.basic.utils.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadManager {

    public static ScheduledExecutorService service = Executors.newScheduledThreadPool(10);

    public static ScheduledFuture scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay) {
        return service.scheduleWithFixedDelay(runnable, initialDelay, delay, TimeUnit.SECONDS);
    }

    public static ScheduledFuture schedule(Runnable runnable, int delay) {
        return service.schedule(runnable, delay, TimeUnit.SECONDS);
    }

    public static void execute(Runnable runnable) {
         service.execute(runnable);
    }

}
