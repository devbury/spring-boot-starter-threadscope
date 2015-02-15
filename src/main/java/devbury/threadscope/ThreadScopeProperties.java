package devbury.threadscope;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "threadScope")
public class ThreadScopeProperties {

    private int poolSize = 25;
    private String threadNamePrefix = "async-";

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
}
