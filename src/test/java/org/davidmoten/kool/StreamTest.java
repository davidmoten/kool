package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.davidmoten.guavamini.Lists;

public class StreamTest {

    @Test
    public void testMapFilter() {
        assertEquals(1, (int) //
        Stream.of(1, 2) //
                .map(x -> x + 1) //
                .filter(x -> x > 2) //
                .count());
    }

    @Test
    public void testPrepend() {
        assertEquals(Lists.newArrayList(0, 1, 2, 3), Stream.of(1, 2, 3).prepend(0).toJavaArrayList());
    }

    @Test
    public void testPrependMany() {
        assertEquals(Lists.newArrayList(0, 1, 2, 3), //
                Stream.of(2, 3) //
                        .prepend(new Integer[] { 0, 1 }) //
                        .toJavaArrayList());
    }

    @Test
    public void testReduce1() {
        assertEquals(10, (int) Stream.of(1, 2, 3, 4).reduce((a, b) -> a + b).get());
    }

    @Test
    public void testFlatMapEmpty() {
        assertTrue(Stream.of(1, 2, 3).flatMap(x -> Stream.<Integer>empty()).isEmpty());
    }

    @Test
    public void testFlatMapEmptyThenSomething() {
        assertEquals(Lists.newArrayList(4, 5), Stream.of(1, 2, 3) //
                .flatMap(x -> {
                    if (x < 3)
                        return Stream.<Integer>empty();
                    else
                        return Stream.of(4, 5);
                }) //
                .toJavaArrayList());
    }

    @Test
    public void testFlatMapToSomething() {
        assertEquals(Lists.newArrayList(10, 11, 20, 21, 30, 31), Stream.of(1, 2, 3) //
                .flatMap(x -> Stream.of(x * 10, x * 10 + 1)) //
                .toJavaArrayList());
    }
}
