package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Ignore;
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
    public void testForEachDispose() {
        checkTrue(b -> Stream.of(1, 2).doOnDispose(() -> b.set(true)).forEach());
    }

    @Test
    public void testCountDispose() {
        checkTrue(b -> Stream.of(1, 2).doOnDispose(() -> b.set(true)).count());
    }

    @Test
    public void testFirstDispose() {
        checkTrue(b -> Stream.of(1, 2).doOnDispose(() -> b.set(true)).first());
    }

    private static void checkTrue(Consumer<AtomicBoolean> consumer) {
        AtomicBoolean b = new AtomicBoolean();
        consumer.accept(b);
        assertTrue(b.get());
    }

    @Test
    public void testPrepend() {
        Stream.of(1, 2, 3).prepend(0).test().assertValuesOnly(0, 1, 2, 3);
    }

    @Test
    public void testPrependMany() {
        Stream.of(2, 3) //
                .prepend(new Integer[] { 0, 1 }) //
                .test() //
                .assertValuesOnly(0, 1, 2, 3);
    }

    @Test
    public void testReduceWithNoInitialValue() {
        assertEquals(10, (int) Stream.of(1, 2, 3, 4).reduce((a, b) -> a + b).get());
    }

    @Test
    public void testReduceWithInitialValue() {
        assertEquals(20, (int) Stream.of(1, 2, 3, 4).reduceWithInitialValue(10, (a, b) -> a + b));
    }

    @Test
    public void testFlatMapEmpty() {
        assertTrue(Stream.of(1, 2, 3).flatMap(x -> Stream.<Integer>empty()).isEmpty());
    }

    @Test
    public void testFlatMapEmptyThenSomething() {
        Stream.of(1, 2, 3) //
                .flatMap(x -> {
                    if (x < 3)
                        return Stream.<Integer>empty();
                    else
                        return Stream.of(4, 5);
                }) //
                .test() //
                .assertValuesOnly(4, 5);
    }

    @Test
    public void testFlatMapToSomething() {
        Stream.of(1, 2, 3) //
                .flatMap(x -> Stream.of(x * 10, x * 10 + 1)) //
                .test() //
                .assertValuesOnly(10, 11, 20, 21, 30, 31);
    }

    @Test
    public void testFlatMapDispose() {
        AtomicBoolean sourceDisposed = new AtomicBoolean();
        AtomicInteger others = new AtomicInteger();
        Stream.of(1, 2).doOnDispose(() -> sourceDisposed.set(true))
                .flatMap(x -> Stream.of(x).doOnDispose(() -> others.incrementAndGet())).count();
        assertTrue(sourceDisposed.get());
        assertEquals(2, others.get());
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
        Stream.of(1, 2, 3).take(2).test().assertValuesOnly(1, 2);
    }

    @Test
    public void testTakeMoreThanAvailable() {
        Stream.of(1, 2, 3).take(100).test().assertValuesOnly(1, 2, 3);
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
        Stream.range(1, 3).test().assertValuesOnly(1L, 2L, 3L);
    }

    @Test
    public void testOrdinals() {
        Stream.ordinals().take(3).test().assertValuesOnly(1L, 2L, 3L);
    }

    @Test
    public void testDefer() {
        Stream.defer(() -> Stream.of(1, 2, 3)).test().assertValuesOnly(1, 2, 3);
    }

    @Test
    public void testTransform() {
        Stream.of(1, 2).transform(s -> s.map(x -> x + 3)).test().assertValuesOnly(4, 5);
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
        Stream.of(1, 2, 3) //
                .switchOnError(e -> Stream.of(4)).test().assertValuesOnly(1, 2, 3);
    }

    @Test
    public void testSwitchOnErrorWhenError() {
        Stream.error(new RuntimeException()) //
                .switchOnError(e -> Stream.of(4)).test().assertValuesOnly(4);
    }

    @Test
    public void testConcat() {
        Stream.of(1, 2).concatWith(Stream.of(3, 4)).test().assertValuesOnly(1, 2, 3, 4);
    }

    @Test
    public void testConcatEmpties() {
        assertTrue(Stream.empty().concatWith(Stream.empty()).isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testConcatIteratorNextWhenNoneAvailable() {
        Stream.empty().concatWith(Stream.empty()).iterator().next();
    }

    @Test
    public void testDoOnComplete() {
        List<Integer> list = new ArrayList<>();
        Stream.of(1, 2) //
                .doOnNext(n -> list.add(n)) //
                .doOnComplete(() -> list.add(3)) //
                .forEach();
        assertEquals(Lists.newArrayList(1, 2, 3), list);
    }

    @Test
    public void testZip() {
        Stream.of(1, 2) //
                .zipWith(Stream.of(3, 4), (x, y) -> x * y) //
                .test() //
                .assertValuesOnly(3, 8);
    }

    @Test
    public void testUsing() {
        Stream.using(() -> new BufferedReader(new StringReader("hello\nthere")), //
                r -> Stream.lines(r)) //
                .test() //
                .assertValues("hello", "there");
    }

    @Test
    public void testJoin() {
        assertEquals("helloAthere", Stream.of("hello", "there").join("A"));
    }

    @Test
    public void testSplit() {
        Stream.of("az", "zb", "c").split("zz").test().assertValuesOnly("a", "bc");
    }

    @Test
    public void testSplitDelimiterNotFound() {
        Stream.of("a", "b", "c").split("z").test().assertValuesOnly("abc");
    }

    @Test
    public void testSplitEmpty() {
        Stream.empty().split("z").test().assertNoValuesOnly();
    }

    @Test
    public void testSplit2() {
        Stream.of("a", "zz", "b").split("zz").test().assertValuesOnly("a", "b");
    }

    @Test
    public void testSplitWithTwoConsecutiveDelimiters() {
        Stream.of("a", "zz", "b").split("z").test().assertValuesOnly("a", "", "b");
    }

    @Test
    public void testSplitManyInOneItem() {
        Stream.of("azbzc").split("z").test().assertValuesOnly("a", "b", "c");
    }

    @Test
    public void testReadCsv() {
        Stream.lines(new File("src/test/resources/test.txt")) //
                .flatMap(line -> Stream.of(line, ",")) //
                .split(",") //
                .map(String::trim) //
                .map(Integer::parseInt) //
                .test() //
                .assertValuesOnly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBufferWithLeftover() {
        Stream.of(1, 2, 3, 4, 5) //
                .buffer(2) //
                .test() //
                .assertValuesOnly(Lists.newArrayList(1, 2), Lists.newArrayList(3, 4),
                        Lists.newArrayList(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBufferExact() {
        Stream.of(1, 2, 3, 4) //
                .buffer(2) //
                .test() //
                .assertValuesOnly(Lists.newArrayList(1, 2), Lists.newArrayList(3, 4));
    }

    @Test
    public void testBufferEmpty() {
        Stream.empty().buffer(2).test().assertNoValuesOnly();
    }

    @Test
    public void testBufferDispose() {
        checkTrue(b -> Stream.of(1, 2).doOnDispose(() -> b.set(true)).buffer(1).first());
    }

    @Test
    public void testSkip() {
        Stream.of(1, 2, 3, 4).skip(2).test().assertValuesOnly(3, 4);
    }

    @Test
    public void testSkipMoreThanAvailable() {
        Stream.of(1, 2, 3, 4).skip(5).test().assertNoValuesOnly();
    }

    @Test
    public void testTakeDisposes() {
        AtomicBoolean disposed = new AtomicBoolean();
        Stream.of(1, 2, 3).doOnDispose(() -> disposed.set(true)).take(2).forEach();
        assertTrue(disposed.get());
    }

    @Test
    public void testSorted() {
        Stream.of(1, 3, 2).sorted().test().assertValuesOnly(1, 2, 3);
    }

    @Test
    public void testSortedDisposes() {
        AtomicBoolean disposed = new AtomicBoolean();
        Stream.of(1, 2, 3).doOnDispose(() -> disposed.set(true)) //
                .sorted() //
                .forEach();
        assertTrue(disposed.get());
    }

    @Test(expected = ClassCastException.class)
    public void testSortedNotComparable() {
        Stream.of(new Object(), new Object()) //
                .sorted() //
                .forEach();
    }

    @Test
    public void testRepeat() {
        Stream.of(1, 2).repeat(2).test().assertValuesOnly(1, 2, 1, 2);
    }

    @Test
    public void testTakeUntil() {
        Stream.of(1, 2, 3, 4, 5).takeUntil(x -> x > 3).test().assertValuesOnly(1, 2, 3);
    }

    @Test
    public void testTakeUntilDisposes() {
        AtomicBoolean disposed = new AtomicBoolean();
        Stream.of(1, 2, 3, 4, 5) //
                .doOnDispose(() -> disposed.set(true)) //
                .takeUntil(x -> x > 3) //
                .test() //
                .assertValuesOnly(1, 2, 3);
        assertTrue(disposed.get());
    }

    @Test
    public void testTakeWhile() {
        Stream.of(1, 2, 3, 4, 5).takeWhile(x -> x <= 3).test().assertValuesOnly(1, 2, 3);
    }

    @Test
    public void testTakewhileDisposes() {
        AtomicBoolean disposed = new AtomicBoolean();
        Stream.of(1, 2, 3, 4, 5) //
                .doOnDispose(() -> disposed.set(true)) //
                .takeWhile(x -> x <= 3) //
                .test() //
                .assertValuesOnly(1, 2, 3);
        assertTrue(disposed.get());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void testBufferWhile() {
        Stream.of(1, 2, 3) //
                .bufferWhile((list, t) -> list.size() == 2, true) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2), //
                        Lists.newArrayList(3));
    }

}
