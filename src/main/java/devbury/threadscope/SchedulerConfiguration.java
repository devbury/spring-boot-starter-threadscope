package devbury.threadscope;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.Executor;

@ConditionalOnMissingBean(AsyncConfigurer.class)
public class SchedulerConfiguration implements AsyncConfigurer {

    @Autowired
    private ThreadScopeManager threadScopeManager;

    @Autowired
    private ThreadScopeProperties threadScopeProperties;

    @Bean
    @ConditionalOnMissingBean(TaskExecutor.class)
    public ThreadScopePropagatingScheduler defaultThreadScopePropagatingScheduler() {
        ThreadScopePropagatingScheduler threadScopePropagatingScheduler = new ThreadScopePropagatingScheduler
                (threadScopeManager);
        threadScopePropagatingScheduler.setPoolSize(threadScopeProperties.getPoolSize());
        threadScopePropagatingScheduler.setThreadNamePrefix(threadScopeProperties.getThreadNamePrefix());
        return threadScopePropagatingScheduler;
    }

    @Bean
    @ConditionalOnMissingBean(AsyncUncaughtExceptionHandler.class)
    public AsyncUncaughtExceptionHandler defaultAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    @Override
    public Executor getAsyncExecutor() {
        return defaultThreadScopePropagatingScheduler();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return defaultAsyncUncaughtExceptionHandler();
    }
}
