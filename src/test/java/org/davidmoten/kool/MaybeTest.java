package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MaybeTest {
    
    @Test
    public void testMaybeOf() {
        assertEquals(1, (int) Maybe.of(1).get().get());
    }
    
}
