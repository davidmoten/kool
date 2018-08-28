package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
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
        assertEquals(Lists.newArrayList(0, 1, 2, 3), Stream.of(1, 2, 3).prepend(0).toList());
    }

    @Test
    public void testPrependMany() {
        assertEquals(Lists.newArrayList(0, 1, 2, 3), //
                Stream.of(2, 3) //
                        .prepend(new Integer[] { 0, 1 }) //
                        .toList());
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
                .toList());
    }

    @Test
    public void testFlatMapToSomething() {
        assertEquals(Lists.newArrayList(10, 11, 20, 21, 30, 31), Stream.of(1, 2, 3) //
                .flatMap(x -> Stream.of(x * 10, x * 10 + 1)) //
                .toList());
    }

    @Test
    public void testFirstOfEmpty() {
        assertFalse(Stream.empty().first().isPresent());
    }

    @Test
    public void testFirst() {
        assertEquals(1, (int) Stream.of(1, 2, 3).first().get());
    }

    @Test
    public void testLastOfEmpty() {
        assertFalse(Stream.empty().last().isPresent());
    }

    @Test
    public void testLast() {
        assertEquals(3, (int) Stream.of(1, 2, 3).last().get());
    }

    @Test
    public void testOnValue() {
        List<Integer> list = new ArrayList<>();
        Stream.of(1, 2, 3).doOnNext(x -> list.add(x)).count();
        assertEquals(Lists.newArrayList(1, 2, 3), list);
    }

    @Test
    public void testTakeEmpty() {
        Stream.empty().take(1).isEmpty();
    }

    @Test
    public void testTakeElements() {
        assertEquals(Lists.newArrayList(1, 2), Stream.of(1, 2, 3).take(2).toList());
    }

    @Test
    public void testTakeMoreThanAvailable() {
        assertEquals(Lists.newArrayList(1, 2, 3), Stream.of(1, 2, 3).take(100).toList());
    }

    @Test
    public void testGetEmpty() {
        assertFalse(Stream.empty().get(0).isPresent());
    }

    @Test
    public void testGetWithin() {
        assertEquals(2, (int) Stream.of(1, 2, 3).get(1).get());
    }

    @Test
    public void testRangeOnEmpty() {
        assertTrue(Stream.range(0, 0).isEmpty());
    }

    @Test
    public void testRange() {
        assertEquals(Lists.newArrayList(1L, 2L, 3L), Stream.range(1, 3).toList());
    }

    @Test
    public void testOrdinals() {
        assertEquals(Lists.newArrayList(1L, 2L, 3L), Stream.ordinals().take(3).toList());
    }

    @Test
    public void testDefer() {
        assertEquals(Lists.newArrayList(1, 2, 3), Stream.defer(() -> Stream.of(1, 2, 3)).toList());
    }

    @Test
    public void testTransform() {
        assertEquals(Lists.newArrayList(4, 5), Stream.of(1, 2).transform(s -> s.map(x -> x + 3)).toList());
    }

    @Test
    public void testCollect() {
        assertEquals(Lists.newArrayList(1, 2, 3),
                Stream.of(1, 2, 3).collect(() -> new ArrayList<>(), (c, x) -> c.add(x)));
    }

    @Test
    public void testDoOnError() {
        Throwable[] err = new Throwable[1];
        try {
            Stream.error(new RuntimeException("boo")) //
                    .doOnError(e -> err[0] = e) //
                    .forEach();
            Assert.fail();
        } catch (RuntimeException t) {
            assertEquals("boo", t.getMessage());
            assertEquals("boo", err[0].getMessage());
        }
    }

    @Test
    public void testSwitchOnErrorNoError() {
        assertEquals(Lists.newArrayList(1, 2, 3), Stream.of(1, 2, 3) //
                .switchOnError(e -> Stream.of(4)).toList());
    }
    
    @Test
    public void testSwitchOnErrorWhenError() {
        assertEquals(Lists.newArrayList(4), Stream.error(new RuntimeException()) //
                .switchOnError(e -> Stream.of(4)).toList());
    }

}
