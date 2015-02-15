package devbury.threadscope;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ThreadScopeStateTest {

    boolean destructionCallbackCalled = false;

    @Test
    public void testFinalize() throws Throwable {
        ThreadScopeState victim = new ThreadScopeState();
        victim.addDestructionCallback("bean", new Runnable() {
            @Override
            public void run() {
                destructionCallbackCalled = true;
            }
        });
        victim.finalize();

        assertTrue(destructionCallbackCalled);
    }

    @Test
    public void testBeansSize() {
        ThreadScopeState victim = new ThreadScopeState();
        assertEquals(0, victim.size());
        victim.addBean("bean", this);
        assertEquals(1, victim.size());
        assertEquals(this, victim.getBean("bean"));
        assertEquals(1, victim.size());
        victim.removeBean("bean");
        assertEquals(0, victim.size());
    }
}