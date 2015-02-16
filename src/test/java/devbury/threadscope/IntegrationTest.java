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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IntegrationTest.class)
@WebAppConfiguration
@EnableAutoConfiguration
public class IntegrationTest {

    @Autowired
    ValueBean valueBean;

    @Autowired
    TaskExecutor taskExecutor;

    @Autowired
    AsyncConfigurer asyncConfigurer;

    @Test
    public void verifyDefaultExecutor() {
        assertEquals(ThreadScopePropagatingScheduler.class, taskExecutor.getClass());
    }

    @Test
    public void verifyDefaultAsyncConfigurer() {
        assertEquals(SchedulerConfiguration.class, asyncConfigurer.getClass());
        assertEquals(ThreadScopePropagatingScheduler.class, asyncConfigurer.getAsyncExecutor().getClass());
        assertEquals(SimpleAsyncUncaughtExceptionHandler.class, asyncConfigurer.getAsyncUncaughtExceptionHandler()
                .getClass());
    }

    @Test
    public void canAccessBeanFromAsync() throws Exception {
        valueBean.setValue("ONE");
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                assertEquals("ONE", valueBean.getValue());
                valueBean.setValue("TWO");
            }
        });
        Thread.sleep(100);
        assertEquals("TWO", valueBean.getValue());
    }

    @Bean
    @Scope("request")
    public ValueBean valueBean() {
        return new ValueBean();
    }

    public class ValueBean {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}