package devbury.threadscope.integration;

import devbury.threadscope.ThreadScopeManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletRequestEvent;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServletRequestListenerTest.class})
@WebIntegrationTest("server.port:0")
@EnableAutoConfiguration
@ComponentScan
public class ServletRequestListenerTest {

    static boolean requestDestroyedCalled = false;

    @Value("${local.server.port}")
    int port;

    @Test
    public void verifyServletRequestListenerRegisteredAndCalled() throws Exception {
        TestRestTemplate restTemplate = new TestRestTemplate();
        restTemplate.getForObject("http://localhost:{port}/test", String.class, port);
        assertTrue(requestDestroyedCalled);
    }

    @Bean
    public ThreadScopeManager threadScopeManager() {
        return new ThreadScopeManager() {
            @Override
            public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
                requestDestroyedCalled = true;
                super.requestDestroyed(servletRequestEvent);
            }
        };
    }
}

