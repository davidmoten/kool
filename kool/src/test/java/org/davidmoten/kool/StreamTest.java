package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.davidmoten.kool.exceptions.CompositeException;
import org.davidmoten.kool.exceptions.UncheckedException;
import org.davidmoten.kool.function.Consumer;
import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.function.Predicates;
import org.junit.Assert;
import org.junit.Test;

import com.github.davidmoten.guavamini.Lists;
import com.github.davidmoten.guavamini.Sets;

public final class StreamTest {

    @Test
    public void testMapFilter() {
        Stream.of(1, 2) //
                .map(x -> x + 1) //
                .filter(x -> x > 2) //
                .count() //
                .test() //
                .assertValue(1L);
    }

    @Test
    public void testFilterWithAlwaysTrueDoesNotModifyStreamAtAll() {
        Stream<Integer> s = Stream.of(1, 2);
        assertTrue(s.filter(Predicates.alwaysTrue()) == s);
    }

    @Test
    public void testForEachDispose() {
        checkTrue(b -> Stream.of(1, 2).doOnDispose(() -> b.set(true)).forEach());
    }

    @Test
    public void testCountDispose() {
        checkTrue(b -> Stream.of(1, 2).doOnDispose(() -> b.set(true)).count().get());
    }

    @Test
    public void testFirstDispose() {
        checkTrue(b -> Stream.of(1, 2).doOnDispose(() -> b.set(true)).first().get());
    }

    private static void checkTrue(Consumer<AtomicBoolean> consumer) {
        AtomicBoolean b = new AtomicBoolean();
        try {
            consumer.accept(b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertTrue(b.get());
    }

    @Test
    public void testPrepend() {
        Stream.of(1, 2, 3).prepend(0).test().assertValuesOnly(0, 1, 2, 3);
    }

    @Test
    public void testPrependIterable() {
        Stream.of(1, 2, 3).prepend(Arrays.asList(5, 6, 7)).test().assertValuesOnly(5, 6, 7, 1, 2, 3);
    }

    @Test
    public void testPrependMany() {
        Stream.of(2, 3) //
                .prepend(new Integer[] { 0, 1 }) //
                .test() //
                .assertValuesOnly(0, 1, 2, 3);
    }

    @Test
    public void testPrependStream() {
        Stream.of(2, 3).prepend(Stream.of(1)).test().assertValuesOnly(1, 2, 3);
    }

    @Test
    public void testReduceWithNoInitialValue() {
        Stream.of(1, 2, 3, 4).reduce((a, b) -> a + b).test().assertValue(10);
    }

    @Test
    public void testReduceWithInitialValue() {
        Stream.of(1, 2, 3, 4).reduce(10, (a, b) -> a + b).test().assertValue(20);
    }

    @Test
    public void testFlatMapEmpty() {
        Stream.of(1, 2, 3).flatMap(x -> Stream.<Integer>empty()).isEmpty().test().assertValue(true);
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
                .flatMap(x -> Stream.of(x).doOnDispose(() -> others.incrementAndGet())).count().get();
        assertTrue(sourceDisposed.get());
        assertEquals(2, others.get());
    }

    @Test
    public void testFlatMapMaybe() {
        Stream.of(1, 2).flatMap(x -> Maybe.of(x)).test().assertValues(1, 2);
    }

    @Test
    public void testFlatMapSingle() {
        Stream.of(1, 2).flatMap(x -> Single.of(x)).test().assertValues(1, 2);
    }

    @Test
    public void testFirstOfEmpty() {
        Stream.empty().first().test().assertNoValue();
    }

    @Test
    public void testFirst() {
        Stream.of(1, 2, 3).first().test().assertValue(1);
    }

    @Test
    public void testLastOfEmpty() {
        Stream.empty().last().test().assertNoValue();
    }

    @Test
    public void testLast() {
        Stream.of(1, 2, 3).last().test().assertValue(3);
    }

    @Test
    public void testOnValue() {
        List<Integer> list = new ArrayList<>();
        Stream.of(1, 2, 3).doOnNext(x -> list.add(x)).count().get();
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
        Stream.empty().get(0).test().assertNoValue();
    }

    @Test
    public void testGetWithin() {
        Stream.of(1, 2, 3).get(1).test().assertValue(2);
    }

    @Test
    public void testRangeOnEmpty() {
        Stream.range(0, 0).isEmpty().test().assertValue(true);
    }

    @Test
    public void testRange() {
        Stream.range(1, 3).test().assertValuesOnly(1, 2, 3);
    }

    @Test
    public void testOrdinals() {
        Stream.ordinals().take(3).test().assertValuesOnly(1, 2, 3);
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
    public void testCompose() {
        Stream.of(1, 2).compose(s -> s.map(x -> x + 3)).test().assertValuesOnly(4, 5);
    }

    @Test
    public void testToStreamJava() {
        assertEquals(2, Stream.of(1, 2).toStreamJava().count());
    }

    @Test
    public void testToMap() {
        Map<Integer, Integer> map = Stream.of(1, 2, 3).toMap(x -> x + 1, x -> 2 * x).get();
        Map<Integer, Integer> expected = new HashMap<>();
        expected.put(2, 2);
        expected.put(3, 4);
        expected.put(4, 6);
        assertEquals(expected, map);
    }

    @Test
    public void testCollect() {
        Stream.of(1, 2, 3) //
                .collect(ArrayList::new, (c, x) -> c.add(x)) //
                .test() //
                .assertValue(Lists.newArrayList(1, 2, 3));
    }

    @Test
    public void testErrorFromCallable() {
        Stream.error(() -> new IOException("boo")).test().assertError(UncheckedException.class);
    }

    @Test
    public void testDoOnErrorWithErrorSource() {
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
    public void testFromCallable() {

    }

    @Test
    public void testDoOnErrorWhenThrownFromOperator() {
        Throwable[] err = new Throwable[1];
        try {
            Stream.of(1).map(x -> {
                throw new RuntimeException("boo");
            }) //
                    .doOnError(e -> err[0] = e) //
                    .forEach();
            Assert.fail();
        } catch (RuntimeException t) {
            assertEquals("boo", t.getMessage());
            assertEquals("boo", err[0].getMessage());
        }
    }

    @Test
    public void testDoOnErrorWhenNoError() {
        Throwable[] err = new Throwable[1];
        Stream.of(1) //
                .doOnError(e -> err[0] = e) //
                .test() //
                .assertNoError() //
                .assertValues(1);
        assertNull(err[0]);
    }

    @Test
    public void testSwitchOnErrorNoError() {
        Stream.of(1, 2, 3) //
                .switchOnError(e -> Stream.of(4)).test().assertValuesOnly(1, 2, 3);
    }

    @Test
    public void testSwitchOnErrorWhenError() {
        Stream.error(new RuntimeException()) //
                .switchOnError(e -> Stream.of(4, 5)).test().assertValuesOnly(4, 5);
    }

    @Test
    public void testSwitchOnErrorHasNextThrows() {
        Stream.from(new Iterable<Integer>() {

            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {

                    @Override
                    public boolean hasNext() {
                        throw new RuntimeException("boo");
                    }

                    @Override
                    public Integer next() {
                        return 1;
                    }

                };
            }
        }).switchOnError(e -> Stream.of(4, 5)) //
                .test() //
                .assertValuesOnly(4, 5);
    }

    @Test
    public void testSwitchOnErrorHasNextThrowsAltIsNull() {
        Stream.from(new Iterable<Integer>() {

            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {

                    @Override
                    public boolean hasNext() {
                        throw new RuntimeException("boo");
                    }

                    @Override
                    public Integer next() {
                        return 1;
                    }

                };
            }
        }).switchOnError(e -> null).test().assertError(NullPointerException.class);
    }

    @Test
    public void testSwitchOnErrorNextThrows() {
        Stream.from(new Iterable<Integer>() {

            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {

                    @Override
                    public boolean hasNext() {
                        return true;
                    }

                    @Override
                    public Integer next() {
                        throw new RuntimeException("boo");
                    }

                };
            }
        }).switchOnError(e -> Stream.of(4, 5)).test().assertValuesOnly(4, 5);
    }

    @Test
    public void testSwitchOnErrorNextThrowsAltIsNull() {
        Stream.from(new Iterable<Integer>() {

            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {

                    @Override
                    public boolean hasNext() {
                        return true;
                    }

                    @Override
                    public Integer next() {
                        throw new RuntimeException("boo");
                    }

                };
            }
        }).switchOnError(e -> null).test().assertError(NullPointerException.class);
    }

    @Test
    public void testConcatWith() {
        Stream.of(1, 2).concatWith(Stream.of(3, 4)).test().assertValuesOnly(1, 2, 3, 4);
    }

    @Test
    public void testConcatWithIterable() {
        Stream.of(1, 2).concatWith(Arrays.asList(3, 4)).test().assertValuesOnly(1, 2, 3, 4);
    }

    @Test
    public void testConcatEmpties() {
        Stream.empty().concatWith(Stream.empty()).isEmpty().test().assertValue(true);
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
    public void testDoOnCompleteCount() {
        List<Integer> list = new ArrayList<>();
        Stream.of(1, 1, 1) //
                .doOnComplete(count -> list.add(count.intValue())) //
                .forEach();
        assertEquals(Lists.newArrayList(3), list);
    }

    @Test
    public void testZip() {
        Stream.of(1, 2) //
                .zipWith(Stream.of(3, 4), (x, y) -> x * y) //
                .test() //
                .assertValuesOnly(3, 8);
    }

    @Test
    public void testZipDifferentLengths() {
        Stream.of(1, 2) //
                .zipWith(Stream.of(3, 4, 5), (x, y) -> x * y) //
                .test() //
                .assertValues(3, 8) //
                .assertError(e -> {
                    return e.getMessage().startsWith("streams must have same length");
                });
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
        Stream.of("hello", "there").join("A").test().assertValue("helloAthere");
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

    @Test
    public void testBufferWithLeftover() {
        Stream.of(1, 2, 3, 4, 5) //
                .buffer(2) //
                .test() //
                .assertNoError()
                .assertValuesOnly(Lists.newArrayList(1, 2), Lists.newArrayList(3, 4), Lists.newArrayList(5));
    }

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

    @Test(expected = IllegalArgumentException.class)
    public void testBufferBadStepThrows() {
        Stream.empty().buffer(2, 0);
    }

    @Test
    public void testBufferDispose() {
        checkTrue(b -> Stream.of(1, 2).doOnDispose(() -> b.set(true)).buffer(1).first().get());
    }
    
    @Test
    public void testBufferNoCopyUsedCorrectly() {
        String s = Stream //
           .of(1, 2, 3) //
           .buffer(2, 1, false) //
           .map(x -> x.toString()) //
           .join(",") //
           .get();
        assertEquals("[1, 2],[2, 3],[3]", s);
    }
    
    @Test
    public void testBufferNoCopyWhenUsedIncorrectly() {
        long count = Stream //
           .of(1, 2, 3) //
           .buffer(2, 1, false) //
           // accumulate, which we should not do with no-copy buffer
           .toList() //
           .flatMap(list -> Stream.from(list)) //
           .filter(list -> list.isEmpty()) //
           .count() //
           .get();
        // assert that 3 empty lists returned
        assertEquals(3, count);
    }

    @Test
    public void testAfterDispose() {
        List<Integer> list = new ArrayList<Integer>();
        Stream //
                .using(() -> list, x -> Stream.of(1, 2), x -> x.add(2)) //
                .doAfterDispose(() -> list.add(1)) //
                .forEach();
        assertEquals(Lists.newArrayList(2, 1), list);
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
    public void testSkipLast() {
        Stream.of(1, 2, 3, 4).skipLast(2).test().assertValuesOnly(1, 2);
    }

    @Test
    public void testSkipLastZero() {
        Stream.of(1, 2, 3, 4).skipLast(0).test().assertValuesOnly(1, 2, 3, 4);
    }

    @Test
    public void testSkipLastMoreThanAvailable() {
        Stream.of(1, 2, 3, 4).skipLast(5).test().assertNoValuesOnly();
    }

    @Test(expected = NoSuchElementException.class)
    public void testSkipLastNoSuchElement() {
        Stream.empty().skipLast(5).iterator().next();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSkipLastNegative() {
        Stream.of(1, 2, 3, 4).skipLast(-1);
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
    public void testRepeatInfinite() {
        Stream.of(1, 2).repeat().take(3).test().assertValuesOnly(1, 2, 1);
    }

    @Test
    public void testRepeatElement() {
        Stream.repeatElement(2, 3).test().assertValuesOnly(2, 2, 2);
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

    @Test
    public void testBufferWhile() {
        Stream.of(1, 2, 3) //
                .bufferWhile((list, t) -> list.size() <= 1, true) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2), //
                        Lists.newArrayList(3));
    }

    @Test
    public void testBufferUntil() {
        Stream.of(1, 2, 3) //
                .bufferUntil((list, t) -> t == 3, true) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3));
    }

    @Test
    public void testBufferUntilEmitMultiple() {
        Stream.of(1, 2, 3, 2, 3) //
                .bufferUntil((list, t) -> t == 3, true) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3), //
                        Lists.newArrayList(2, 3));
    }

    @Test
    public void testBufferUntilEmitMultipleEmitRemainderTrue() {
        Stream.of(1, 2, 3, 2, 4) //
                .bufferUntil((list, t) -> t == 3, true) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3), //
                        Lists.newArrayList(2, 4));
    }

    @Test
    public void testBufferUntilEmitMultipleEmitRemainderFalse() {
        Stream.of(1, 2, 3, 2, 4) //
                .bufferUntil((list, t) -> t == 3, false) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3));
    }

    @Test
    public void testBufferWhileNoRemainder() {
        Stream.of(1, 2, 3, 4) //
                .bufferWhile((list, t) -> list.size() <= 1, false) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2));
    }

    @Test
    public void testBufferUntilRemainderEqualsFalse() {
        Stream.of(1, 2, 3) //
                .bufferUntil((list, t) -> list.size() == 2, false) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3));
    }

    @Test
    public void testBufferUntilRemainderEqualsFalseEmitsAll() {
        Stream.of(1, 2, 3) //
                .bufferUntil((list, t) -> list.size() == 3, false) //
                .test() //
                .assertNoValuesOnly();
    }

    @Test
    public void testBufferWhileRemainderEqualsFalseDoesNotEmitAnything() {
        Stream.of(1, 2, 3) //
                .bufferWhile((list, t) -> list.size() != 3, false) //
                .test() //
                .assertNoValuesOnly();
    }

    @Test
    public void testBufferUntilWithStepLargerThanBufferedList() {
        Stream.of(1, 2, 3, 4, 5, 6, 7) //
                .bufferUntil((list, t) -> list.size() == 1, true, 3, 100) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2), //
                        Lists.newArrayList(4, 5), //
                        Lists.newArrayList(7));
    }

    @Test
    public void testBufferWhileWithStepLargerThanBufferedList() {
        Stream.of(1, 2, 3, 4, 5, 6, 7) //
                .bufferWhile((list, t) -> list.size() < 2, true, 3, 100) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2), //
                        Lists.newArrayList(4, 5), //
                        Lists.newArrayList(7));
    }

    @Test
    public void testBufferUntilWithStepSmallerThanBufferedList() {
        Stream.of(1, 2, 3, 4, 5, 6, 7) //
                .bufferUntil((list, t) -> list.size() == 3, true, 1, 100) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3, 4), //
                        Lists.newArrayList(2, 3, 4, 5), //
                        Lists.newArrayList(3, 4, 5, 6), //
                        Lists.newArrayList(4, 5, 6, 7), //
                        Lists.newArrayList(5, 6, 7), //
                        Lists.newArrayList(6, 7), //
                        Lists.newArrayList(7));
    }
    
    @Test
    public void testBufferUntilWithStepSmallerThanBufferedListUseBuilder() {
        Stream.of(1, 2, 3, 4, 5, 6, 7) //
                .bufferUntil()
                .arrayList()
                .condition((list, t) -> list.size() == 3)
                .step(1)
                .emitRemainder(true)
                .maxReplay(100)
                .build()
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3, 4), //
                        Lists.newArrayList(2, 3, 4, 5), //
                        Lists.newArrayList(3, 4, 5, 6), //
                        Lists.newArrayList(4, 5, 6, 7), //
                        Lists.newArrayList(5, 6, 7), //
                        Lists.newArrayList(6, 7), //
                        Lists.newArrayList(7));
    }

    @Test
    public void testBufferWhileWithStepSmallerThanBufferedList() {
        Stream.of(1, 2, 3, 4, 5, 6, 7) //
                .bufferWhile((list, t) -> list.size() <= 3, true, 1, 100) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3, 4), //
                        Lists.newArrayList(2, 3, 4, 5), //
                        Lists.newArrayList(3, 4, 5, 6), //
                        Lists.newArrayList(4, 5, 6, 7), //
                        Lists.newArrayList(5, 6, 7), //
                        Lists.newArrayList(6, 7), //
                        Lists.newArrayList(7));
    }

    @Test
    public void testBufferUntilWithStepSmallerThanBufferedListDontEmitRemainder() {
        Stream.of(1, 2, 3, 4, 5, 6, 7) //
                .bufferUntil((list, t) -> list.size() == 3, false, 1, 100) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3, 4), //
                        Lists.newArrayList(2, 3, 4, 5), //
                        Lists.newArrayList(3, 4, 5, 6), //
                        Lists.newArrayList(4, 5, 6, 7));
    }

    @Test
    public void testBufferWhileWithStepSmallerThanBufferedListDontEmitRemainder() {
        Stream.of(1, 2, 3, 4, 5, 6, 7) //
                .bufferWhile((list, t) -> list.size() <= 3, false, 1, 100) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3, 4), //
                        Lists.newArrayList(2, 3, 4, 5), //
                        Lists.newArrayList(3, 4, 5, 6));
    }
    
    @Test
    public void testBufferWhileWithStepSmallerThanBufferedListDontEmitRemainderUseBuilder() {
        Stream.of(1, 2, 3, 4, 5, 6, 7) //
                .bufferWhile() //
                .arrayList() //
                .condition((list, t) -> list.size() <= 3) //
                .step(1) //
                .emitRemainder(false) //
                .maxReplay(100)
                .build()
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3, 4), //
                        Lists.newArrayList(2, 3, 4, 5), //
                        Lists.newArrayList(3, 4, 5, 6));
    }
    
    @Test
    public void testBufferWhileWithStepSmallerThanBufferedListDontEmitRemainderUseBuilderWithFactory() {
        Stream.of(1, 2, 3, 4, 5, 6, 7) //
                .bufferWhile() //
                .factory(ArrayList::new) //
                .condition((list, t) -> list.size() <= 3) //
                .accumulator((list, t)-> {list.add(t); return list;}) //
                .step(1) //
                .emitRemainder(false) //
                .maxReplay(100)
                .build()
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2, 3, 4), //
                        Lists.newArrayList(2, 3, 4, 5), //
                        Lists.newArrayList(3, 4, 5, 6));
    }

    @Test(expected = NoSuchElementException.class)
    public void testBufferUntilWithStepWhenEmptyAndNextCalled() {
        Stream.empty().bufferWhile((list, t) -> true, true, 1, 100).iterator().next();

    }

    @Test
    public void testBufferWhileExceedsMaxBufferSize() {
        Stream.range(1, 10) //
                .bufferWhile((list, t) -> true, true, 1, 2) //
                .test() //
                .assertError(IllegalArgumentException.class); // cannot replay enough when maxSize is 2
    }

    @Test
    public void testLinesFromResource() {
        Stream.linesFromResource("/test3.txt").test().assertValuesOnly("hello", "there", "world");
    }

    @Test
    public void testLinesWhenFactoryThrows() {
        Stream.lines(() -> {
            throw new RuntimeException("boo");
        }) //
                .test() //
                .assertNoValues() //
                .assertErrorMessage("boo");
    }

    @Test
    public void testLinesFromResource2() {
        Stream.linesFromResource("/test3.txt", StandardCharsets.UTF_8).test().assertValuesOnly("hello", "there",
                "world");
    }

    @Test
    public void testLinesFromFactoryThatThrows() {
        Stream.lines(() -> {
            throw new IOException();
        }) //
                .test() //
                .assertError(UncheckedException.class);
    }

    @Test
    public void testLinesFromFileThatDoesNotExist() {
        Stream.lines(new File("THIS_FILE_DOES_NOT_EXIST")).test().assertError(UncheckedIOException.class);
    }

    @Test
    public void testSingleToStream() {
        Single.of(1).toStream().test().assertValues(1);
    }

    @Test
    public void testSingleToStreamIterator() {
        StreamIterator<Integer> it = Single.of(1).toStream().iterator();
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertEquals(1, (int) it.next());
        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindFirst() {
        Stream.of(1, 2, 3, 4).findFirst(x -> x > 2).test().assertValue(3);
    }

    @Test
    public void testFindFirstAlwaysFalse() {
        Stream.of(1, 2, 3, 4).findFirst(Predicates.alwaysFalse()).test().assertNoValue();
    }

    @Test
    public void testMapWithIndex() {
        Stream.of("a", "b", "c") //
                .mapWithIndex() //
                .test() //
                .assertValues(Indexed.create("a", 0), Indexed.create("b", 1), Indexed.create("c", 2));
    }

    @Test
    public void testSkipUntil() {
        Stream.of(1, 2, 3, 4, 5).skipUntil(x -> x > 2).test().assertValues(3, 4, 5);
    }

    @Test
    public void testSkipUntilNoValuesFound() {
        Stream.of(1, 2, 3, 4, 5).skipUntil(x -> x > 5).test().assertNoValues();
    }

    @Test
    public void testSkipUntilOnEmpty() {
        Stream.<Integer>empty().skipUntil(x -> x > 5).test().assertNoValues();
    }

    @Test
    public void testSkipWhile() {
        Stream.of(1, 2, 3, 4, 5).skipWhile(x -> x <= 2).test().assertValues(3, 4, 5);
    }

    @Test
    public void testEvery() {
        List<Long> list = Lists.newArrayList();
        Stream.range(0, 1000) //
                .every(100, (count, x) -> {
                    list.add(count);
                }).forEach();
        assertEquals(Lists.newArrayList(100L, 200L, 300L, 400L, 500L, 600L, 700L, 800L, 900L, 1000L), list);
    }

    @Test
    public void testReplay() {
        AtomicInteger count = new AtomicInteger();
        Stream<Integer> stream = Stream.of(1, 2, 3).doOnNext(x -> count.incrementAndGet()).cache();
        stream.test().assertValues(1, 2, 3);
        assertEquals(3, count.get());
        stream.test().assertValues(1, 2, 3);
        assertEquals(3, count.get());
    }

    @Test
    public void testDoOnEmptyDoesNotFireIfStreamNonEmpty() {
        AtomicBoolean b = new AtomicBoolean();
        Stream.of(1, 2).doOnEmpty(() -> b.set(true)).forEach();
        assertFalse(b.get());
    }

    @Test
    public void testDoOnEmptyFiresIfStreamEmpty() {
        AtomicBoolean b = new AtomicBoolean();
        Stream.empty().doOnEmpty(() -> b.set(true)).forEach();
        assertTrue(b.get());
    }

    @Test
    public void testSwitchOnEmptyIfNotEmpty() {
        Stream.of(1, 2).switchOnEmpty(() -> Stream.of(3)).test().assertValues(1, 2);
    }

    @Test
    public void testSwitchOnEmptyIfEmpty() {
        Stream.empty().switchOnEmpty(() -> Stream.of(3)).test().assertValues(3);
    }

    @Test
    public void testByteBuffersOneElementOutput() {
        ByteBuffer bb = Stream
                .byteBuffers(() -> new ByteArrayInputStream("hello there".getBytes(StandardCharsets.UTF_8)), 100)
                .single() //
                .get();
        byte[] x = new byte[bb.remaining()];
        bb.get(x);
        assertEquals("hello there", new String(x, StandardCharsets.UTF_8));
    }

    @Test
    public void testByteBuffersWithDefaultBufferSizeOneElementOutput() {
        ByteBuffer bb = Stream
                .byteBuffers(() -> new ByteArrayInputStream("hello there".getBytes(StandardCharsets.UTF_8))).single() //
                .get();
        byte[] x = new byte[bb.remaining()];
        bb.get(x);
        assertEquals("hello there", new String(x, StandardCharsets.UTF_8));
    }

    @Test
    public void testByteBuffersManyElementsOutput() {
        byte[] b = Stream.byteBuffers(() -> new ByteArrayInputStream("hello there".getBytes(StandardCharsets.UTF_8)), 2)
                .collect(() -> new ByteArrayOutputStream(), (c, bb) -> {
                    while (bb.position() < bb.limit()) {
                        c.write(bb.get());
                    }
                }).get().toByteArray();
        assertEquals("hello there", new String(b, StandardCharsets.UTF_8));
    }

    @Test
    public void testBytesWithFactory() {
        Stream.bytes(() -> new ByteArrayInputStream("hello there".getBytes(StandardCharsets.UTF_8)))
                .map(x -> new String(x, StandardCharsets.UTF_8)) //
                .test() //
                .assertValues("hello there");
    }

    @Test
    public void testBytes() {
        Stream.bytes(new ByteArrayInputStream("hello there".getBytes(StandardCharsets.UTF_8)))
                .map(x -> new String(x, StandardCharsets.UTF_8)) //
                .test() //
                .assertValues("hello there");
    }

    @Test
    public void testBytesManyElementsOutput() {
        byte[] b = Stream.bytes(() -> new ByteArrayInputStream("hello there".getBytes(StandardCharsets.UTF_8)), 2)
                .collect(() -> new ByteArrayOutputStream(), (c, bytes) -> {
                    try {
                        c.write(bytes);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }).get() //
                .toByteArray();
        assertEquals("hello there", new String(b, StandardCharsets.UTF_8));
    }

    @Test
    public void testMergeInterleavedEmpty() {
        Stream.merge(Stream.empty()).test().assertNoValues();
    }

    @Test
    public void testMergeInterleavedOneStreamOneValue() {
        Stream.merge(Stream.of(1)).test().assertValues(1);
    }

    @Test
    public void testMergeInterleavedOneStreamManyValues() {
        Stream.merge(Stream.of(1, 2, 3)).test().assertValues(1, 2, 3);
    }

    @Test
    public void testMergeInterleavedOneStreamOneStreamWithEmpty() {
        Stream.merge(Stream.of(1, 2, 3), Stream.empty()).test().assertValues(1, 2, 3);
    }

    @Test
    public void testMergeInterleavedOneStreamEmptyWithOneStream() {
        Stream.merge(Stream.empty(), Stream.of(1, 2, 3)).test().assertValues(1, 2, 3);
    }

    @Test
    public void testMergeInterleavedTwoStreamsSameSize() {
        Stream.merge(Stream.of(1, 3, 5), Stream.of(2, 4, 6)).test().assertValues(1, 2, 3, 4, 5, 6);
    }

    @Test
    public void testMergeInterleavedFirstStreamBiggerThanSecond() {
        Stream.merge(Stream.of(1, 3, 5, 6, 7), Stream.of(2, 4)).test().assertValues(1, 2, 3, 4, 5, 6, 7);
    }

    @Test
    public void testMergeInterleavedDisposalErrors() {
        Stream<Integer> a = Stream.using(() -> 1, x -> Stream.of(x), x -> {
            throw new RuntimeException("" + x);
        });

        Stream<Integer> b = Stream.using(() -> 2, x -> Stream.of(x), x -> {
            throw new RuntimeException("" + x);
        });

        Stream.merge(a, b) //
                .test() //
                .assertValues(1, 2) //
                .assertError(CompositeException.class);
    }

    @Test
    public void testMax() {
        Stream.of(1, 5, 2) //
                .max(Comparator.naturalOrder()) //
                .test() //
                .assertValue(5);
    }

    @Test
    public void testMaxOfOne() {
        Stream.of(1) //
                .max(Comparator.naturalOrder()) //
                .test() //
                .assertValue(1);
    }

    @Test
    public void testMaxOfNone() {
        Stream.<Integer>empty() //
                .max(Comparator.naturalOrder()) //
                .test() //
                .assertNoValue();
    }

    @Test
    public void testMin() {
        Stream.of(5, 1, 2) //
                .min(Comparator.naturalOrder()) //
                .test() //
                .assertValue(1);
    }

    @Test
    public void testAllReturnsTrue() {
        Stream.of(1, 2, 3).all(x -> x < 4).test().assertValue(true);
    }

    @Test
    public void testAllReturnsFalse() {
        Stream.of(1, 2, 3).all(x -> x < 3).test().assertValue(false);
    }

    @Test
    public void testAllOfEmptyReturnsTrue() {
        Stream.<Integer>empty().all(x -> x < 3).test().assertValue(true);
    }

    @Test
    public void testAnyReturnsTrue() {
        Stream.of(1, 2, 3).any(x -> x == 2).test().assertValue(true);
    }

    @Test
    public void testAnyReturnsFalse() {
        Stream.of(1, 2, 3).any(x -> x == 5).test().assertValue(false);
    }

    @Test
    public void testAnyOfEmptyReturnsFalse() {
        Stream.<Integer>empty().any(x -> x == 5).test().assertValueOnly(false);
    }

    @Test
    public void testDistinctUntilChanged() {
        Stream.of(1, 1, 2, 3, 3, 4, 4, 4).distinctUntilChanged().test().assertValuesOnly(1, 2, 3, 4);
    }

    @Test
    public void testDistinctUntilChangedOnEmpty() {
        Stream.empty().distinctUntilChanged().test().assertNoValuesOnly();
    }

    @Test
    public void testTakeLast() {
        Stream.of(1, 2, 3).takeLast(2).test().assertValuesOnly(2, 3);
    }

    @Test
    public void testTakeLastWhenExceedsAvailableLength() {
        Stream.of(1, 2, 3).takeLast(5).test().assertValuesOnly(1, 2, 3);
    }

    @Test
    public void testTakeLastWhenEmpty() {
        Stream.<Integer>empty().takeLast(5).test().assertNoValuesOnly();
    }

    @Test
    public void testTakeLastEarlyDisposal() {
        AtomicBoolean b = new AtomicBoolean();
        StreamIterator<Integer> it = Stream.of(1, 2, 3).doOnDispose(() -> b.set(true)).takeLast(2).iterator();
        it.dispose();
        assertTrue(b.get());
    }

    @Test
    public void testGenerateOneValueAndImmediatelyComplete() {
        Stream.<Integer>generate(emitter -> {
            emitter.onNext(1);
            emitter.onComplete();
        }) //
                .test() //
                .assertValuesOnly(1);
    }

    @Test
    public void testGenerateInfiniteValues() {
        AtomicInteger i = new AtomicInteger();
        Stream.<Integer>generate(emitter -> {
            i.incrementAndGet();
            emitter.onNext(i.get());
        }) //
                .limit(5) //
                .test() //
                .assertValuesOnly(1, 2, 3, 4, 5);

    }

    @Test
    public void testGenerateConsumerDoesNotCallEmitter() {
        Stream.<Integer>generate(emitter -> {
        }) //
                .test().assertError(IllegalStateException.class);
    }

    @Test
    public void testGenerateConsumerCallsOnNextTwice() {
        Stream.<Integer>generate(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
        }) //
                .test() //
                .assertError(IllegalArgumentException.class);
    }

    @Test
    public void testToMaybeFromEmpty() {
        Stream.empty().maybe().test().assertNoValue();
    }

    @Test
    public void testToMaybeFromOne() {
        Stream.of(1).maybe().test().assertValue(1);
    }

    @Test
    public void testToMaybeFromTwo() {
        Stream.of(1, 2).maybe().test().assertError(IllegalStateException.class);
    }

    @Test
    public void testTo() {
        Stream.of(1, 2).to(stream -> stream.count()).test().assertValueOnly(2L);
    }

    @Test
    public void testFromArrayEmpty() {
        Stream.fromArray(new Integer[] {}).test().assertNoValuesOnly();
    }

    @Test
    public void testFromArrayOne() {
        Stream.fromArray(new Integer[] { 1 }).test().assertValuesOnly(1);
    }

    @Test
    public void testFromArrayTwo() {
        Stream.fromArray(new Integer[] { 1, 2 }).test().assertValuesOnly(1, 2);
    }

    @Test
    public void testFromArrayPartial() {
        Stream.fromArray(new Integer[] { 1, 2, 3, 4, 5 }, 2, 4).test().assertValuesOnly(3, 4);
    }

    @Test
    public void testFromArrayIntEmpty() {
        Stream.fromArray(new int[] {}).test().assertNoValuesOnly();
    }

    @Test
    public void testFromArrayIntOne() {
        Stream.fromArray(new int[] { 1 }).test().assertValuesOnly(1);
    }

    @Test
    public void testFromArrayIntTwo() {
        Stream.fromArray(new int[] { 1, 2 }).test().assertValuesOnly(1, 2);
    }

    @Test
    public void testFromArrayIntPartial() {
        Stream.fromArray(new int[] { 1, 2, 3, 4, 5 }, 2, 4).test().assertValuesOnly(3, 4);
    }

    @Test
    public void testFromArrayDoubleEmpty() {
        Stream.fromArray(new double[] {}).test().assertNoValuesOnly();
    }

    @Test
    public void testFromArrayDoubleOne() {
        Stream.fromArray(new double[] { 1 }).test().assertValuesOnly(1.0);
    }

    @Test
    public void testFromArrayDoubleTwo() {
        Stream.fromArray(new double[] { 1, 2 }).test().assertValuesOnly(1.0, 2.0);
    }

    @Test
    public void testFromArrayDoublePartial() {
        Stream.fromArray(new double[] { 1, 2, 3, 4, 5 }, 2, 4).test().assertValuesOnly(3.0, 4.0);
    }

    @Test
    public void testFromArrayFloatEmpty() {
        Stream.fromArray(new float[] {}).test().assertNoValuesOnly();
    }

    @Test
    public void testFromArrayFloatOne() {
        Stream.fromArray(new float[] { 1 }).test().assertValuesOnly(1.0f);
    }

    @Test
    public void testFromArrayFloatTwo() {
        Stream.fromArray(new float[] { 1, 2 }).test().assertValuesOnly(1.0f, 2.0f);
    }

    @Test
    public void testFromArrayFloatPartial() {
        Stream.fromArray(new float[] { 1, 2, 3, 4, 5 }, 2, 4).test().assertValuesOnly(3.0f, 4.0f);
    }

    @Test
    public void testReadMeExample() {
        Stream //
                .range(1, 10) //
                .flatMap(n -> Stream //
                        .range(1, n) //
                        .reduce(0, (a, b) -> a + b)) //
                .mapWithIndex(1) //
                .forEach();
    }

    @Test
    public void testIgnoreDisposalError() {
        Stream.using(() -> 1, //
                n -> Stream.of(n), //
                n -> {
                    throw new RuntimeException("boo");
                }) //
                .ignoreDisposalError() //
                .test() //
                .assertValuesOnly(1);
    }

    @Test
    public void testIgnoreDisposalErrorWithConsumer() {
        checkTrue(b -> Stream.using(() -> 1, //
                n -> Stream.of(n), //
                n -> {
                    throw new RuntimeException("boo");
                }) //
                .ignoreDisposalError(t -> b.set(true)) //
                .test() //
                .assertValuesOnly(1));
    }

    @Test
    public void testRangeLong() {
        Stream.rangeLong(1, 3).test().assertValuesOnly(1L, 2L, 3L);
    }

    @Test
    public void testOrdinalsLong() {
        Stream.ordinalsLong().take(3).test().assertValuesOnly(1L, 2L, 3L);
    }

    @Test
    public void testRepeatElementInfinte() {
        assertEquals("a", Stream.repeatElement("a").take(100).last().get().get());
    }

    @Test
    public void testHasElements() {
        assertTrue(Stream.of(1, 2, 3).hasElements().get());
    }

    @Test
    public void testToSet() {
        assertEquals(Sets.newHashSet(1, 2), Stream.of(1, 2).toSet().get());
    }

    @Test
    public void testDistinctEmpty() {
        Stream.empty().distinct().test().assertNoValues();
    }

    @Test
    public void testDistinct() {
        Stream.of(1, 1, 2, 3, 1, 2, 4, 3).distinct().test().assertValuesOnly(1, 2, 3, 4);
    }

    @Test
    public void testMaterializeEmpty() {
        Stream.empty().materialize().test().assertValuesOnly(Notification.complete());
    }

    @Test
    public void testMaterializeValue() {
        Stream.of(1).materialize().test().assertValuesOnly(Notification.of(1), Notification.complete());
    }

    @Test
    public void testMaterializeError() {
        RuntimeException ex = new RuntimeException("boo");
        Stream.error(() -> ex).materialize().test().assertValuesOnly(Notification.error(ex));
    }

    @Test
    public void testReverseEmpty() {
        Stream.empty().reverse().test().assertNoValuesOnly();
    }

    @Test
    public void testReverse() {
        Stream.of(1, 2, 3).reverse().test().assertValuesOnly(3, 2, 1);
    }

    @Test(expected = NoSuchElementException.class)
    public void testReverseBeyondLast() {
        StreamIterator<Integer> it = Stream.of(1).reverse().iterator();
        assertEquals(1, (int) it.next());
        it.next();
    }

    @Test
    public void testReverseDisposeEarly() {
        StreamIterator<Integer> it = Stream.of(1).reverse().iterator();
        it.dispose();
        it.dispose();
    }

    @Test
    public void testGroupByList() {
        Map<Integer, List<Integer>> map = new HashMap<>();
        map.put(0, Lists.newArrayList(3, 6, 9));
        map.put(1, Lists.newArrayList(1, 4, 7, 10));
        map.put(2, Lists.newArrayList(2, 5, 8));
        Stream //
                .range(1, 10) //
                .groupByList(i -> i % 3) //
                .test() //
                .assertValueOnly(map);
    }

    @Test
    public void testGroupBySet() {
        Map<Integer, Set<Integer>> map = new HashMap<>();
        map.put(0, Sets.newHashSet(3, 6, 9));
        map.put(1, Sets.newHashSet(1, 4, 7, 10));
        map.put(2, Sets.newHashSet(2, 5, 8));
        Stream //
                .range(1, 10) //
                .repeat(2) //
                .groupBySet(i -> i % 3) //
                .test() //
                .assertValueOnly(map);
    }

    @Test
    public void testOfWith6() {
        Stream.of(1, 2, 3, 4, 5, 6).test().assertValuesOnly(1, 2, 3, 4, 5, 6);
    }

    @Test
    public void testOfWith7() {
        Stream.of(1, 2, 3, 4, 5, 6, 7).test().assertValuesOnly(1, 2, 3, 4, 5, 6, 7);
    }

    @Test
    public void testClass() {
        Stream<Number> s = Stream.<Integer>of(1, 2, 3).cast(Number.class);
        s.test().assertValues(1, 2, 3);
    }

    @Test
    public void testContains() {
        Stream.of(1, 2, 3).contains(2).test().assertValueOnly(true);
    }

    @Test
    public void testPrintStackTrace() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(bytes);
        PrintStream prev = System.err;
        System.setErr(err);
        try {
            Stream.error(new RuntimeException("expected error")) //
                    .printStackTrace() //
                    .switchOnError(e -> Stream.empty()) //
                    .forEach();
            String msg = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
            assertTrue(msg.startsWith("java.lang.RuntimeException: expected error"));
        } finally {
            System.setErr(prev);
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testByteBuffersBadBufferSize() {
        Stream.byteBuffers(new ByteArrayInputStream("boo".getBytes()), -1);
    }

    @Test(expected = NoSuchElementException.class)
    public void testByteBuffersIteratorNext() {
        StreamIterator<ByteBuffer> it = Stream.byteBuffers(new ByteArrayInputStream("boo".getBytes()), 100).iterator();
        it.next();
        it.next();
    }

    @Test
    public void testByteBuffersDisposeCoverage() {
        StreamIterator<ByteBuffer> it = Stream.byteBuffers(new ByteArrayInputStream("boo".getBytes()), 100).iterator();
        it.dispose();
    }

    @Test
    public void testByteBuffersInputStreamThrows() {
        Stream.byteBuffers(throwingInputStream()).test().assertError(UncheckedIOException.class);
    }

    private static InputStream throwingInputStream() {
        return new InputStream() {

            @Override
            public int read() throws IOException {
                throw new IOException("hello");
            }
        };
    }

    @Test
    public void testByteBuffersHasNextRepeatedCalls() {
        StreamIterator<ByteBuffer> it = Stream.byteBuffers(new ByteArrayInputStream("boo".getBytes()), 100).iterator();
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
    }

    @Test
    public void testLinesBufferedReaderEmpty() {
        Stream.lines(new BufferedReader(new StringReader(""))).test().assertNoValuesOnly();
    }

    @Test(expected = NoSuchElementException.class)
    public void testLinesReadBeyondEnd() {
        StreamIterator<String> it = Stream.lines(new BufferedReader(new StringReader(""))).iterator();
        it.next();
    }

    @Test
    public void testLinesDisposeBeforeHasNext() {
        StreamIterator<String> it = Stream.lines(new BufferedReader(new StringReader("abc"))).iterator();
        it.dispose();
        assertFalse(it.hasNext());
    }

    @Test
    public void testLinesReaderThrowss() {
        Stream.lines(new BufferedReader(new InputStreamReader(throwingInputStream()))) //
                .test() //
                .assertError(UncheckedIOException.class);
    }

    @Test
    public void testReduceEmpty() {
        Stream.<Integer>empty().reduce((x, y) -> x + y).test().assertNoValue();
    }

    @Test
    public void testReduceOneValue() {
        Stream.of(1).reduce((x, y) -> x + y).test().assertNoValue();
    }

    @Test
    public void testInterval() {
        List<Long> times = new ArrayList<>();
        long t = System.currentTimeMillis();
        Stream.interval(100, TimeUnit.MILLISECONDS) //
                .doOnNext(x -> times.add(System.currentTimeMillis())) //
                .take(3) //
                .test() //
                .assertValuesOnly(0, 1, 2);
        assertEquals(3, times.size());
        assertTrue(times.get(0) < t + 100);
        assertTrue(times.get(1) >= t + 100);
        assertTrue(times.get(2) >= t + 200);
        assertTrue(times.get(2) < t + 500);
    }

    @Test
    public void testDelayStart() {
        long[] time = new long[1];
        long t = System.currentTimeMillis();
        Stream //
                .of(1) //
                .delayStart(200, TimeUnit.MILLISECONDS) //
                .doOnNext(x -> time[0] = System.currentTimeMillis()) //
                .test() //
                .assertValuesOnly(1);
        assertTrue(time[0] >= t + 200);
    }

    @Test(expected = NoSuchElementException.class)
    public void testMergeBeyondNext() {
        StreamIterator<Integer> it = Stream.of(1).mergeWith(Stream.of(2)).iterator();
        assertEquals(1, (int) it.next());
        assertEquals(2, (int) it.next());
        it.next();
    }

    @Test
    public void testNotificationEquals() {
        assertEquals(Notification.of(1), Notification.of(1));
    }

    @Test
    public void testNotificationErrorEquals() {
        RuntimeException error = new RuntimeException("boo");
        assertEquals(Notification.of(error), Notification.of(error));
    }

    @Test
    public void testNotificationGetters() {
        Notification<Integer> n = Notification.of(1);
        assertEquals(1, (int) n.value());
        assertTrue(n.hasValue());
        assertFalse(n.isError());
        assertFalse(n.isComplete());
    }

    @Test
    public void testNotificationErrorGetters() {
        RuntimeException ex = new RuntimeException("boo");
        Notification<Integer> n = Notification.error(ex);
        assertFalse(n.hasValue());
        assertTrue(n.isError());
        assertFalse(n.isComplete());
        assertEquals("boo", n.error().getMessage());
    }

    @Test
    public void testNotificationHashcode() {
        assertEquals(962, Notification.of(1).hashCode());
    }

    @Test
    public void testNotificationEquals1() {
        assertEquals(Notification.of(1), Notification.of(1));
        assertNotEquals(Notification.of(1), Notification.of(2));
        Notification<Integer> n = Notification.of(1);
        assertTrue(n.equals(n));
        assertFalse(n.equals(null));
    }

    @Test
    public void testNotificationEquals2() {
        RuntimeException error = new RuntimeException("boo");
        Notification<Integer> n = Notification.error(error);
        assertNotEquals(n, Notification.of(1));
        assertNotEquals(Notification.of(1), n);
        assertEquals(n, n);
        assertEquals(n, Notification.error(error));
    }

    @Test
    public void testNotificationEquals3() {
        RuntimeException error = new RuntimeException("boo");
        Notification<Integer> n = Notification.error(error);
        assertEquals(n, n);
        assertNotEquals(n, Notification.error(new IOException()));
    }

    @Test
    public void testNotificationEqualsAnotherClass() {
        assertNotEquals(Notification.of(1), new Object());
    }

    @Test
    public void testMaterializeWithoutError() {
        Stream.of(1).materialize().test().assertValuesOnly(Notification.of(1), Notification.complete());
    }

    @Test
    public void testNotificationComplete() {
        Notification<Object> n = Notification.complete();
        assertTrue(n.isComplete());
        assertFalse(n.isError());
    }

    @Test
    public void testMaterialize() {
        RuntimeException error = new RuntimeException("boo");
        Stream.of(1).concatWith(Stream.<Integer>error(error)) //
                .materialize() //
                .test() //
                .assertValues(Notification.of(1), Notification.error(error));
    }

    @Test
    public void forEachConsumer() {
        List<Integer> list = new ArrayList<>();
        Stream.of(1, 2).forEach(x -> list.add(x));
        assertEquals(Lists.newArrayList(1, 2), list);
    }

    @Test
    public void testDematerializeEmpty() {
        Stream.empty().dematerialize(x -> null).test().assertError(NoSuchElementException.class);
    }

    @Test
    public void testDematerializeRealNotifications() {
        Stream.of(Notification.of(1), Notification.of(2), Notification.complete()) //
                .dematerialize(Function.identity()) //
                .test() //
                .assertValuesOnly(1, 2);
    }

    @Test
    public void testDematerializeRealNotificationsAndError() {
        RuntimeException error = new RuntimeException("boo");
        Stream.of(Notification.of(1), Notification.of(2), Notification.error(error)) //
                .dematerialize(Function.identity()) //
                .test() //
                .assertValues(1, 2) //
                .assertError(e -> e == error);
    }

    @Test
    public void testDematerializeRealNotificationsNoTerminalEvent() {
        Stream.of(Notification.of(1), Notification.of(2)) //
                .dematerialize(Function.identity()) //
                .test() //
                .assertValues(1, 2) //
                .assertError(NoSuchElementException.class);
    }

    @Test
    public void testRetryWhenTwoRetriesAndSucceeds() {
        AtomicInteger count = new AtomicInteger();
        Stream.<Integer>defer(() -> {
            if (count.incrementAndGet() <= 2) {
                throw new RuntimeException("boo");
            } else {
                return Stream.of(1);
            }
        }) //
                .retryWhen(err -> Single //
                        .timer(1, TimeUnit.MILLISECONDS)) //
                .test() //
                .assertValues(1);
    }

    @Test
    public void testRetryWhenAlwaysFailsAndSucceedsWithBuilder() {
        AtomicInteger count = new AtomicInteger();
        Stream.error(() -> new RuntimeException("boo")) //
                .doOnStart(() -> count.incrementAndGet()) //
                .retryWhen() //
                .maxRetries(3) //
                .build() //
                .test() //
                .assertNoValues() //
                .assertError(x -> x.getMessage().equals("boo"));
        assertEquals(4, count.get());
    }

    @Test
    public void testRetryWhenDelays() {
        long t = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger();
        Stream.error(() -> new RuntimeException("boo")) //
                .doOnStart(count::incrementAndGet) //
                .retryWhen() //
                .delays(Stream.of(30L, 30L), TimeUnit.MILLISECONDS) //
                .build() //
                .test() //
                .assertNoValues() //
                .assertError(x -> x.getMessage().equals("boo"));
        assertEquals(3, count.get());
        // check retry delay happened
        assertTrue(System.currentTimeMillis() - t >= 60);
    }

    @Test
    public void testRetryWhenDelay() {
        long t = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger();
        int delayMs = 30;
        int maxRetries = 3;
        Stream.error(() -> new RuntimeException("boo")) //
                .doOnStart(count::incrementAndGet) //
                .retryWhen() //
                .delay(delayMs, TimeUnit.MILLISECONDS) //
                .maxRetries(maxRetries) //
                .build() //
                .test() //
                .assertNoValues() //
                .assertError(x -> x.getMessage().equals("boo"));
        assertEquals(maxRetries + 1, count.get());
        assertTrue(System.currentTimeMillis() - t >= maxRetries * delayMs);
    }

    @Test
    public void testRetryWhenPredicateTrue() {
        AtomicInteger count = new AtomicInteger();
        int maxRetries = 3;
        Stream.error(() -> new RuntimeException("boo")) //
                .doOnStart(count::incrementAndGet) //
                .retryWhen() //
                .isTrue(e -> true) //
                .maxRetries(maxRetries).build() //
                .test() //
                .assertNoValues() //
                .assertError(x -> x.getMessage().equals("boo"));
        assertEquals(maxRetries + 1, count.get());
    }

    @Test
    public void testRetryWhenPredicateFalse() {
        AtomicInteger count = new AtomicInteger();
        int maxRetries = 3;
        Stream.error(() -> new RuntimeException("boo")) //
                .doOnStart(count::incrementAndGet) //
                .retryWhen() //
                .isTrue(e -> false) //
                .maxRetries(maxRetries).build() //
                .test() //
                .assertNoValues() //
                .assertError(x -> x.getMessage().equals("boo"));
        assertEquals(1, count.get());
    }

    @Test
    public void testRepeatLastEmpty() {
        Stream.empty().repeatLast().test().assertNoValuesOnly();
    }

    @Test
    public void testRepeatLastOne() {
        Stream.of(1).repeatLast().take(4).test().assertValuesOnly(1, 1, 1, 1);
    }

    @Test
    public void testRepeatLastOneWithNumRepeats() {
        Stream.of(1).repeatLast(3).test().assertValuesOnly(1, 1, 1, 1);
    }

    @Test
    public void testRepeatLastTwoWithNumRepeats() {
        Stream.of(1, 2).repeatLast(3).test().assertValuesOnly(1, 2, 2, 2, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRepeatLastWithNegativeCount() {
        Stream.of(1, 2).repeatLast(-1);
    }

    @Test
    public void testRepeatLastWithCountZero() {
        Stream<Integer> x = Stream.of(1, 2);
        assertTrue(x == x.repeatLast(0));
    }

    @Test
    public void testCollector() {
        Stream.of(4, 5).collect(Collectors.counting()).test().assertValue(2L);
    }

    @Test
    public void testSumInt() {
        Stream.of(1, 2, 3).sumInt(Function.identity()).test().assertValue(6);
    }

    @Test
    public void testSumLong() {
        Stream.of(1L, 2L, 3L).sumLong(Function.identity()).test().assertValue(6L);
    }

    @Test
    public void testSumDouble() {
        Stream.of(1.0, 2.0, 3.0).sumDouble(Function.identity()).test().assertValue(6.0);
    }

    @Test
    public void testFromReader() {
        Stream.from(new StringReader("")).test().assertValuesOnly();
    }

    @Test
    public void testFromReaderOneLetter() {
        Stream.from(new StringReader("a")).test().assertValuesOnly("a");
    }

    @Test
    public void testFromReaderBuffered() {
        Stream.from(new StringReader("abcdefg"), 3).test().assertValuesOnly("abc", "def", "g");
    }

    @Test
    public void testFromInputStream() {
        String s = "hello there how are you";
        InputStream in = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        Stream.from(in).reduce((x, y) -> x + y).test().assertValue(s);
    }

    @Test
    public void testFromInputStreamSmallBufferSize() {
        String s = "hello there how are you";
        InputStream in = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        Stream.from(in, StandardCharsets.UTF_8, 2).reduce((x, y) -> x + y).test().assertValue(s);
    }

    @Test
    public void testStringsFromBytesStream() {
        Stream.strings(Stream.of("hello there".getBytes(StandardCharsets.UTF_8))) //
                .test() //
                .assertValues("hello there").assertNoError();
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyIteratorGoesTooFar() {
        StreamIterator<Object> it = Stream.empty().iterator();
        it.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void testRepeatLastIteratorWithEmpty() {
        StreamIterator<Object> it = Stream.empty().repeatLast().iterator();
        it.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void testRepeatLastIteratorBeyondOne() {
        StreamIterator<Integer> it = Stream.of(1).repeatLast(1).iterator();
        assertEquals(1, (int) it.next());
        assertEquals(1, (int) it.next());
        it.next();
    }

    @Test
    public void testRepeatLastEarlyDispose() {
        StreamIterator<Integer> it = Stream.of(1).repeatLast(1000).first().iterator();
        assertEquals(1, (int) it.next());
        assertFalse(it.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyNext() {
        StreamIterator<Object> it = Stream.empty().iterator();
        assertFalse(it.hasNext());
        it.next();
    }

    @Test
    public void testExists() {
        assertTrue(Stream.of(1, 2, 3).exists(x -> x == 1).get());
    }

    @Test
    public void testExistsReturnsFalse() {
        assertFalse(Stream.of(1, 2, 3).exists(x -> x == 4).get());
    }
    
    @Test
    public void testNoneMatch() {
        assertTrue(Stream.of(1, 2, 3).noneMatch(x -> x == 4).get());
    }

    @Test
    public void testNoneMatchReturnsFalse() {
        assertFalse(Stream.of(1, 2, 3).noneMatch(x -> x == 1).get());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPowerSet() {
        assertEquals(Lists.newArrayList(//
                Sets.newHashSet(), //
                Sets.newHashSet(1), //
                Sets.newHashSet(2), //
                Sets.newHashSet(1, 2), //
                Sets.newHashSet(3), //
                Sets.newHashSet(1, 3), //
                Sets.newHashSet(2, 3), //
                Sets.newHashSet(1, 2, 3) //
        ), Stream.powerSet(3).toList().get());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPermutations() {
        assertEquals(Lists.newArrayList(//
                Arrays.asList(0, 1, 2), //
                Arrays.asList(0, 2, 1), //
                Arrays.asList(1, 2, 0), //
                Arrays.asList(2, 1, 0), //
                Arrays.asList(2, 0, 1), //
                Arrays.asList(1, 0, 2) //
        ), Stream.permutations(3).toList().get());
    }

    @Test
    public void testPermutationsSizeZero() {
        assertEquals(Arrays.asList(new ArrayList<Integer>()), //
                Stream.permutations(0).toList().get());
    }

    @Test
    public void testPermutationsSizeOne() {
        assertEquals(Arrays.asList(Arrays.asList(0)), //
                Stream.permutations(1).toList().get());
    }

    @Test
    public void testComposeSingle() {
        assertEquals(3, Stream.of(1, 2, 4).composeSingle(s -> s.count()).get().longValue());
    }

    @Test
    public void testComposeMaybe() {
        assertEquals(4, Stream.of(1, 2, 4).composeMaybe(s -> s.last()).get().get().longValue());
    }

    @Test
    public void testStatistics() {
        assertEquals(2.5, Stream.of(1, 2, 3, 4).statistics(x -> x).get().mean(), 0.00001);
    }

    @Test
    public void testStatisticsForReadme() {
        Stream.of(1, 2, 3, 4).statistics(x -> x).get().toString("", "\n");
    }

    @Test
    public void testJavaStreamEarlyClosure() {
        AtomicBoolean disposed = new AtomicBoolean();
        java.util.stream.Stream<Integer> s = Stream //
                .of(1, 2, 3) //
                .doOnDispose(() -> disposed.set(true)) //
                .toStreamJava();
        assertEquals(1, (int) s.findFirst().get());
        s.close();
        assertTrue(disposed.get());
    }

    @Test
    public void testDoWithIndex() {
        AtomicInteger count = new AtomicInteger();
        List<Long> list = new ArrayList<>();
        Stream.of(1, 1, 1, 1, 1, 1, 1).doWithIndex((i, x) -> {
            if (i % 3 == 0)
                count.incrementAndGet();
            list.add(i);
        }).go();
        assertEquals(Lists.newArrayList(0L, 1L, 2L, 3L, 4L, 5L, 6L), list);
        assertEquals(3, count.get());
    }

    @Test
    public void testFlatMapGenerator() {
        List<String> list = Stream //
                .of(1) //
                .<String>flatMap((x, consumer) -> consumer.accept(x + "")) //
                .toList() //
                .get();
        assertEquals(Arrays.asList("1"), list);
    }

    @Test
    public void testFlatMapGeneratorWithOnFinish() {
        Single<List<String>> stream = Stream.of(1, 5) //
                .<String>flatMap( //
                        (x, consumer) -> {
                            consumer.accept(x + "");
                            consumer.accept(2 * x + "");
                        }, //
                        consumer -> {
                            consumer.accept("a");
                            consumer.accept("b");
                        })
                .toList();
        assertEquals(Arrays.asList("1", "2", "5", "10", "a", "b"), stream.get());
        assertEquals(Arrays.asList("1", "2", "5", "10", "a", "b"), stream.get());
    }

    @Test
    public void testFlatMapGeneratorEmptyWithOnFinish() {
        List<String> list = Stream.<Integer>empty() //
                .<String>flatMap( //
                        (x, consumer) -> {
                            consumer.accept(x + "");
                            consumer.accept(2 * x + "");
                        }, //
                        consumer -> {
                            consumer.accept("a");
                            consumer.accept("b");
                        })
                .toList().get();
        assertEquals(Arrays.asList("a", "b"), list);
    }

    @Test
    public void testPublisher() {
        Publisher<Integer> p = Stream.of(1, 2, 3).publish();
        p.onNext(4);
        p.onNext(5);
        StreamIterator<Integer> it = p.iterator();
        assertEquals(1, it.next().intValue());
        assertEquals(2, it.next().intValue());
        assertEquals(3, it.next().intValue());
        assertEquals(4, it.next().intValue());
        assertEquals(5, it.next().intValue());
        p.onNext(6);
        assertEquals(6, it.next().intValue());
        assertFalse(it.hasNext());

        // queue empty 
        assertEquals(3, p.count().get().intValue());
    }
    
    public static void main(String[] args) throws MalformedURLException {
        URL url = new URL("https://doesnotexist.zz");
        Stream.using(() -> url.openStream(), in -> Stream.bytes(in))
                .doOnStart(() -> System.out.println("connecting at " + System.currentTimeMillis())) //
                .reduce(0, (n, bytes) -> n + bytes.length) // count bytes
                .retryWhen() //
                .delays(Stream.of(1L, 2L, 4L), TimeUnit.SECONDS) // uses Thread.sleep!
                .build() //
                .doOnError(e -> System.out.println(e.getMessage())) //
                .doOnValue(n -> System.out.println("bytes read=" + n)) //
                .switchOnError(e -> Single.of(-1)) //
                .forEach();
    }
}
