package devbury.threadscope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class ThreadScopeManager implements Scope, ServletRequestListener {
    public static final String THREAD_SCOPE = "thread";

    private static final Logger logger = LoggerFactory.getLogger(ThreadScopeManager.class);

    private final ThreadLocal<ThreadScopeState> threadLocal =
            new NamedThreadLocal<ThreadScopeState>("threadScopeState") {
                @Override
                protected ThreadScopeState initialValue() {
                    return new ThreadScopeState();
                }
            };

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        ThreadScopeState state = getThreadScopeState();
        Object object = state.getBean(name);
        if (object == null) {
            object = objectFactory.getObject();
            state.addBean(name, object);
        }
        return object;
    }

    @Override
    public Object remove(String name) {
        return getThreadScopeState().removeBean(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        getThreadScopeState().addDestructionCallback(name, callback);
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return Thread.currentThread().getName();
    }

    public ThreadScopeState getThreadScopeState() {
        return threadLocal.get();
    }

    public void bind(ThreadScopeState threadScopeState) {
        logger.debug("Binding {} beans to thread", threadScopeState.size());
        threadLocal.set(threadScopeState);
    }

    public void unbind() {
        logger.debug("Unbinding {} beans from thread", getThreadScopeState().size());
        threadLocal.set(new ThreadScopeState());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        unbind();
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        // not used
    }
}
