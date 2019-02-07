package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;

import org.davidmoten.kool.exceptions.TestRuntimeException;
import org.junit.Test;

public class NotificationTest {
    
    @Test
    public void testHashCode() {
        assertEquals(963, Notification.of(2).hashCode());
    }
    
    @Test
    public void testHashCodeWithException() {
        assertEquals(1016, Notification.of(new TestRuntimeException() {

            @Override
            public int hashCode() {
                return 55;
            }
            
        }).hashCode());
    }

}
