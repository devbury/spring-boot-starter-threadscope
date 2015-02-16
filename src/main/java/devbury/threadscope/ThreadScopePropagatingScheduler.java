/*
 * Copyright 2015 devbury LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
                return buildRunnableScheduledFuture(threadScopeManager, task);
            }

            @Override
            protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
                return decorateTask((Callable<V>) null, task);
            }
        };
    }

    protected <V> RunnableScheduledFuture<V> buildRunnableScheduledFuture(ThreadScopeManager threadScopeManager,
                                                                          RunnableScheduledFuture<V> task) {
        return new ThreadScopeRunnableScheduledFuture<>(threadScopeManager, task);
    }
}
