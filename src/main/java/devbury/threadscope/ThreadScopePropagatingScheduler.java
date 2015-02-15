package devbury.threadscope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.*;

@ManagedResource
public class ThreadScopePropagatingScheduler extends ThreadPoolTaskScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadScopePropagatingScheduler.class);

    private final ThreadScopeManager threadScopeManager;

    public ThreadScopePropagatingScheduler(ThreadScopeManager threadScopeManager) {
        this.threadScopeManager = threadScopeManager;
    }

    @Override
    @ManagedAttribute
    public int getActiveCount() {
        return super.getActiveCount();
    }

    @Override
    @ManagedAttribute
    public int getPoolSize() {
        return super.getPoolSize();
    }

    @Override
    @ManagedAttribute
    public void setPoolSize(int poolSize) {
        super.setPoolSize(poolSize);
    }

    @Override
    protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory,
                                                      RejectedExecutionHandler rejectedExecutionHandler) {
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler) {
            @Override
            protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V>
                    task) {
                LOGGER.debug("Decorating Task");
                return new ThreadScopeRunnableScheduledFuture<>(threadScopeManager, task);
            }

            @Override
            protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
                return decorateTask((Callable<V>) null, task);
            }
        };
    }
}
