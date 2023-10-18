package core.colin.basic.utils.schedule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private static ThreadPool instance;
    private final int count = Runtime.getRuntime().availableProcessors();
    private ExecutorService service;

    private ThreadPool() {
    }


    public static synchronized ThreadPool geInstance() {
        synchronized (ThreadPool.class) {
            if (instance == null) {
                instance = new ThreadPool();
            }
        }
        return instance;
    }

    public ExecutorService getService() {
        if (this.service == null) {
            this.service = Executors.newCachedThreadPool();
        }
        return this.service;
    }
}
