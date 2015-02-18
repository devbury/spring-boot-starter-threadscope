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
import org.springframework.beans.factory.ObjectFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds thread scoped bean references for a given thread.  An instance of this class may be shared among
 * asynchronous task threads and the calling thread.
 */
public class ThreadScopeState {
    private static final Logger logger = LoggerFactory.getLogger(ThreadScopeState.class);

    private final Map<String, Object> beansByName = new ConcurrentHashMap<>();

    /**
     * This map is only added to when a thread already has a lock on the beans object and is only read from when the
     * class instance is finalized.  A ConcurrentHashMap is not needed.
     */
    private final Map<String, Runnable> destructionCallbacksByName = new HashMap<>();

    /**
     * Destruction callbacks will trigger when there are no longer any references to ThreadScopeState.  Callbacks
     * will be executed on the jvm finalizer thread.  There may be a delay from when ThreadScopeState is no longer
     * referenced to when the jvm calls the finalizer.
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        beansByName.clear();
        if (!destructionCallbacksByName.isEmpty()) {
            logger.debug("Triggering bean destruction callbacks");
            for (Map.Entry<String, Runnable> entry : destructionCallbacksByName.entrySet()) {
                logger.debug("  Destroying {}", entry.getKey());
                entry.getValue().run();
            }
            destructionCallbacksByName.clear();
        }
    }

    public Object getBean(String name) {
        return beansByName.get(name);
    }

    /**
     * Add The bean if it is not already in the map.
     */
    public void addBean(String name, ObjectFactory<?> objectFactory) {
        synchronized (beansByName) {
            if (beansByName.get(name) == null) {
                beansByName.put(name, objectFactory.getObject());
            }
        }
    }

    public Object removeBean(String name) {
        return beansByName.remove(name);
    }

    public void addDestructionCallback(String name, Runnable callback) {
        destructionCallbacksByName.put(name, callback);
    }

    public int size() {
        return beansByName.size();
    }
}
