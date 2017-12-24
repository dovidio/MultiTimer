package io.dovid.multitimer.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by umber on 12/24/2017.
 */
public class TimerEntityTest {
    @Test
    public void equals() throws Exception {
        TimerEntity t1 = new TimerEntity();
        TimerEntity t2 = new TimerEntity();
        assertTrue(t1.equals(t2));
        assertFalse(t1.equals(null));
        assertFalse(t1.equals(new Object()));

        t1 = new TimerEntity(1, "1", 0, 0, true, true);
        t2 = new TimerEntity(1, "1", 0, 0, true, true);
        assertTrue(t1.equals(t2));

        t2 = new TimerEntity(2, "1", 0, 0, true, true);
        assertFalse(t1.equals(t2));
    }

}