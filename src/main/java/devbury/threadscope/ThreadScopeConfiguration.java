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
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;

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
        String scopeName = environment.getProperty(SCOPE_NAME_PROPERTY, ThreadScopeProperties.DEFAULT_SCOPE_NAME);
        logger.info("Thread scope name set to {}", scopeName);
        customScopeConfigurer.addScope(scopeName, threadScopeManager);
        return customScopeConfigurer;
    }
}
