package org.davidmoten.kool.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RingBufferTest {

    @Test
    public void test() {
        RingBuffer<Integer> r = new RingBuffer<>(2);
        assertEquals(0, r.size());
        assertTrue(r.isEmpty());
        assertNull(r.poll());
        r.add(1).add(2);
        assertFalse(r.isEmpty());
        assertEquals(2, r.size());
        assertEquals(1, (int) r.poll());
        assertEquals(2, (int) r.poll());
        assertTrue(r.isEmpty());
        r.replay(1);
        assertEquals(2, (int) r.poll());
        r.replay(1);
        assertEquals(2, (int) r.poll());
        r.replay(2);
        assertEquals(1, (int) r.poll());
        assertEquals(2, (int) r.poll());
        assertTrue(r.isEmpty());
        r.add(3);
        r.replay(1);
        assertEquals(2, r.size());
        assertEquals(2, (int) r.poll());
        assertEquals(3, (int) r.poll());
    }
    
}
