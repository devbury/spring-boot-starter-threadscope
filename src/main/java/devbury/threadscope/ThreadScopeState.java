package devbury.threadscope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds thread scoped bean references for a given thread.  An instance of this class may be shared among
 * asynchronous task threads and the calling thread.
 */
public class ThreadScopeState {
    private static final Logger logger = LoggerFactory.getLogger(ThreadScopeState.class);

    private final Map<String, Object> beans = new ConcurrentHashMap<>();
    private final Map<String, Runnable> destructionCallbacks = new ConcurrentHashMap<>();

    /**
     * Destruction callbacks will triggered when there are no longer any references to ThreadScopeState.  Callbacks
     * will be executed on the jvm finalizer thread.  There may be a delay from when ThreadScopeState in no longer
     * referenced and the jvm calls the finalizer.
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        beans.clear();
        if (!destructionCallbacks.isEmpty()) {
            logger.debug("Triggering bean destruction callbacks");
            for (Map.Entry<String, Runnable> entry : destructionCallbacks.entrySet()) {
                logger.debug("  Destroying {}", entry.getKey());
                entry.getValue().run();
            }
            destructionCallbacks.clear();
        }
    }

    public Object getBean(String name) {
        return beans.get(name);
    }

    public void addBean(String name, Object bean) {
        beans.put(name, bean);
    }

    public Object removeBean(String name) {
        return beans.remove(name);
    }

    public void addDestructionCallback(String name, Runnable callback) {
        destructionCallbacks.put(name, callback);
    }

    public int size() {
        return beans.size();
    }
}
