package devbury.threadscope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;

import static devbury.threadscope.ThreadScopeManager.THREAD_SCOPE;

@EnableConfigurationProperties
@Import(SchedulerConfiguration.class)
public class ThreadScopeConfiguration {

    public static final String SCOPE_NAME_PROPERTY = "threadScope.scopeName";

    private static final Logger logger = LoggerFactory.getLogger(ThreadScopeConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(ThreadScopeProperties.class)
    public ThreadScopeProperties defaultThreadScopeProperties() {
        return new ThreadScopeProperties();
    }

    @Bean
    @ConditionalOnMissingBean(ThreadScopeManager.class)
    public ThreadScopeManager defaultThreadScopeService() {
        return new ThreadScopeManager();
    }

    @Bean
    public CustomScopeConfigurer threadCustomScopeConfigurer(ThreadScopeManager threadScopeManager,
                                                             ConfigurableEnvironment environment) {
        CustomScopeConfigurer customScopeConfigurer = new CustomScopeConfigurer();
        // ConfigurationProperties not configured yet.  Use ConfigurableEnvironment to get properties.
        String scopeName = environment.getProperty(SCOPE_NAME_PROPERTY, THREAD_SCOPE);
        logger.info("Thread scope name set to {}", scopeName);
        customScopeConfigurer.addScope(scopeName, threadScopeManager);
        return customScopeConfigurer;
    }
}
