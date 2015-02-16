package devbury.threadscope;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static devbury.threadscope.ThreadScopeManager.THREAD_SCOPE;

@ConfigurationProperties(prefix = "threadScope")
public class ThreadScopeProperties {

    private int poolSize = 25;
    private String threadNamePrefix = "async-";
    private String scopeName = THREAD_SCOPE;

    public int getPoolSize() {
        return poolSize;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }
}
