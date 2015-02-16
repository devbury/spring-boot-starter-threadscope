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

import java.util.concurrent.*;

public class ThreadScopeRunnableScheduledFuture<V> implements RunnableScheduledFuture<V> {

    protected final ThreadScopeManager threadScopeManager;
    protected final RunnableScheduledFuture<V> delegate;
    protected final ThreadScopeState threadScopeState;

    public ThreadScopeRunnableScheduledFuture(ThreadScopeManager threadScopeManager, RunnableScheduledFuture<V> delegate) {
        this.threadScopeManager = threadScopeManager;
        this.delegate = delegate;
        this.threadScopeState = threadScopeManager.getThreadScopeState();
    }

    @Override
    public boolean isPeriodic() {
        return delegate.isPeriodic();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return delegate.getDelay(unit);
    }

    @Override
    public int compareTo(Delayed o) {
        return delegate.compareTo(o);
    }

    @Override
    public void run() {
        beforeRun();
        try {
            delegate.run();
        } finally {
            afterRun();
        }
    }

    /**
     * Overload this and {@link #beforeRun()} to add custom bindings.  Binding MDC values in logback is an example.
     */
    protected void afterRun() {
        threadScopeManager.unbind();
    }

    protected void beforeRun() {
        threadScopeManager.bind(threadScopeState);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }
}
