package core.colin.basic.utils.schedule;


import com.blankj.utilcode.util.LogUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ReporterPoolExecutor extends ThreadPoolExecutor {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAX_THREAD_COUNT = CORE_POOL_SIZE;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static ReporterPoolExecutor instance = getInstance();

    private ReporterPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                LogUtils.d("Task rejected, too many task!");
            }
        });
    }

    /**
     * 关于如何设置参数, 这里有个明确的说明
     * https://www.cnblogs.com/waytobestcoder/p/5323130.html
     *
     * @return
     */
    public static ReporterPoolExecutor getInstance() {
        if (null == instance) {
            synchronized (ReporterPoolExecutor.class) {
                if (null == instance) {
                    instance = new ReporterPoolExecutor(CORE_POOL_SIZE, MAX_THREAD_COUNT, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory());
                    instance.allowCoreThreadTimeOut(true);

                }
            }
        }
        return instance;
    }


}
