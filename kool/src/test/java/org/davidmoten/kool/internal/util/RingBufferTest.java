package org.davidmoten.kool.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RingBufferTest {

    private static final int MAX_SIZE = 100;

    @Test
    public void test() {
        RingBuffer<Integer> r = new RingBuffer<>(2, MAX_SIZE);
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

    @Test
    public void testAllocateMore() {
        RingBuffer<Integer> r = new RingBuffer<>(2, MAX_SIZE);
        r.add(1).add(2).add(3).add(4);
        assertEquals(4, r.size());
        assertEquals(1, (int) r.poll());
        assertEquals(2, (int) r.poll());
        assertEquals(3, (int) r.poll());
        assertEquals(4, (int) r.poll());
        assertEquals(0, r.size());
    }

    @Test
    public void testAllocateMoreWhenFinishBeforeStart() {
        RingBuffer<Integer> r = new RingBuffer<>(2, MAX_SIZE);
        r.add(1).poll();
        r.add(2).add(3).add(4);
        r.replay(1);
        assertEquals(1, (int) r.poll());
        assertEquals(2, (int) r.poll());
        assertEquals(3, (int) r.poll());
        r.add(5).add(6).add(7).add(8).add(9).add(10).add(11).add(12);
    }

    @Test(expected = RuntimeException.class)
    public void addMoreThanMaxSize() {
        RingBuffer<Integer> r = new RingBuffer<>(2, 2);
        r.add(1).add(2).add(3).add(4);
    }
}
