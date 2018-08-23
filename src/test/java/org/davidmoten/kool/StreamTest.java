package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;

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
        assertEquals(Lists.newArrayList(0, 1, 2, 3),
                Stream.of(1, 2, 3).prepend(0).toJavaArrayList());
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

}
