package devbury.threadscope;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties
@Import(SchedulerConfiguration.class)
public class ThreadScopeConfiguration {

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
    public CustomScopeConfigurer threadCustomScopeConfigurer(ThreadScopeManager threadScopeManager) {
        CustomScopeConfigurer customScopeConfigurer = new CustomScopeConfigurer();
        customScopeConfigurer.addScope(ThreadScopeManager.THREAD_SCOPE, threadScopeManager);
        return customScopeConfigurer;
    }
}
