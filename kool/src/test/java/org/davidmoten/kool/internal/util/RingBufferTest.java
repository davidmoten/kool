package org.davidmoten.kool.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;

public class RingBufferTest {

    private static final int MAX_SIZE = 100;

    @Test
    public void test() {
        RingBuffer<Integer> r = new RingBuffer<>(MAX_SIZE);
        assertEquals(0, r.size());
        assertTrue(r.isEmpty());
        assertNull(r.poll());
        r.offer(1).offer(2);
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
        r.offer(3);
        r.replay(1);
        assertEquals(2, r.size());
        assertEquals(2, (int) r.poll());
        assertEquals(3, (int) r.poll());
    }

    @Test
    public void testSize() {
        RingBuffer<Integer> r = new RingBuffer<>(4);
        r.offer(1).offer(2).offer(3).offer(4);
        assertEquals(4, r.size());
        assertEquals(1, (int) r.poll());
        assertEquals(2, (int) r.poll());
        assertEquals(3, (int) r.poll());
        assertEquals(4, (int) r.poll());
        assertEquals(0, r.size());
        r.offer(5).offer(6).offer(7);
        assertEquals(3, r.size());
    }

    @Test
    public void testAllocateMoreWhenFinishBeforeStart() {
        RingBuffer<Integer> r = new RingBuffer<>(MAX_SIZE);
        r.offer(1).poll();
        r.offer(2).offer(3).offer(4);
        r.replay(1);
        assertEquals(1, (int) r.poll());
        assertEquals(2, (int) r.poll());
        assertEquals(3, (int) r.poll());
        r.offer(5).offer(6).offer(7).offer(8).offer(9).offer(10).offer(11).offer(12);
    }

    @Test
    public void testGetByIndex() {
        RingBuffer<Integer> r = new RingBuffer<>(MAX_SIZE);
        r.offer(1).offer(2).offer(3).offer(4);
        assertEquals(1, r.get(0).intValue());
        assertEquals(2, r.get(1).intValue());
        assertEquals(3, r.get(2).intValue());
        assertEquals(4, r.get(3).intValue());
        try {
            r.get(4);
            fail();
        } catch (ArrayIndexOutOfBoundsException e) {
            // good
        }
    }

    @Test
    public void testAsList() {
        RingBuffer<Integer> r = new RingBuffer<>(MAX_SIZE);
        r.offer(1).offer(2).offer(3).offer(4);
        assertEquals(Arrays.asList(1, 2, 3, 4), r);
        assertEquals(Arrays.asList(1, 2, 3), r.subList(0, 3).stream().collect(Collectors.toList()));
    }

    @Test(expected = RuntimeException.class)
    public void addMoreThanMaxSize() {
        RingBuffer<Integer> r = new RingBuffer<>(2);
        r.offer(1).offer(2).offer(3).offer(4);
    }
}
