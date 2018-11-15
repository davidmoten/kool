package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.davidmoten.kool.exceptions.CompositeException;
import org.davidmoten.kool.exceptions.UncheckedException;
import org.junit.Assert;
import org.junit.Test;

import com.github.davidmoten.guavamini.Lists;

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
    public void testForEachDispose() {
        checkTrue(b -> Stream.of(1, 2).doOnDispose(() -> b.set(true)).forEach());
    }

    @Test
    public void testCountDispose() {
        checkTrue(b -> Stream.of(1, 2).doOnDispose(() -> b.set(true)).count().get());
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
        Stream.of(1, 2, 3, 4).reduce((a, b) -> a + b).test().assertValue(10);
    }

    @Test
    public void testReduceWithInitialValue() {
        Stream.of(1, 2, 3, 4).reduceWithInitialValue(10, (a, b) -> a + b).test().assertValue(20);
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
        Stream.of(1, 2, 3) //
                .collect(ArrayList::new, (c, x) -> c.add(x)) //
                .test() //
                .assertValue(Lists.newArrayList(1, 2, 3));
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
                .switchOnError(e -> Stream.of(4)).test().assertValuesOnly(4);
    }

    @Test
    public void testConcat() {
        Stream.of(1, 2).concatWith(Stream.of(3, 4)).test().assertValuesOnly(1, 2, 3, 4);
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
                .assertValuesOnly(Lists.newArrayList(1, 2), Lists.newArrayList(3, 4), Lists.newArrayList(5)) //
        ;
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
                .bufferUntil((list, t) -> list.size() == 2, true) //
                .test() //
                .assertValuesOnly( //
                        Lists.newArrayList(1, 2), //
                        Lists.newArrayList(3));
    }

    @Test
    public void testLinesFromResource() {
        Stream.linesFromResource("/test3.txt").test().assertValuesOnly("hello", "there", "world");
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
    public void testBytes() {
        Stream.bytes(() -> new ByteArrayInputStream("hello there".getBytes(StandardCharsets.UTF_8)), 100)
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
                        throw new UncheckedException(e);
                    }
                }).get() //
                .toByteArray();
        assertEquals("hello there", new String(b, StandardCharsets.UTF_8));
    }

    @Test
    public void testMergeInterleavedEmpty() {
        Stream.mergeInterleaved(Stream.empty()).test().assertNoValues();
    }

    @Test
    public void testMergeInterleavedOneStreamOneValue() {
        Stream.mergeInterleaved(Stream.of(1)).test().assertValues(1);
    }

    @Test
    public void testMergeInterleavedOneStreamManyValues() {
        Stream.mergeInterleaved(Stream.of(1, 2, 3)).test().assertValues(1, 2, 3);
    }

    @Test
    public void testMergeInterleavedOneStreamOneStreamWithEmpty() {
        Stream.mergeInterleaved(Stream.of(1, 2, 3), Stream.empty()).test().assertValues(1, 2, 3);
    }

    @Test
    public void testMergeInterleavedOneStreamEmptyWithOneStream() {
        Stream.mergeInterleaved(Stream.empty(), Stream.of(1, 2, 3)).test().assertValues(1, 2, 3);
    }

    @Test
    public void testMergeInterleavedTwoStreamsSameSize() {
        Stream.mergeInterleaved(Stream.of(1, 3, 5), Stream.of(2, 4, 6)).test().assertValues(1, 2, 3, 4, 5, 6);
    }

    @Test
    public void testMergeInterleavedFirstStreamBiggerThanSecond() {
        Stream.mergeInterleaved(Stream.of(1, 3, 5, 6, 7), Stream.of(2, 4)).test().assertValues(1, 2, 3, 4, 5, 6, 7);
    }

    @Test
    public void testMergeInterleavedDisposalErrors() {
        Stream<Integer> a = Stream.using(() -> 1, x -> Stream.of(x), x -> {
            throw new RuntimeException("" + x);
        });

        Stream<Integer> b = Stream.using(() -> 2, x -> Stream.of(x), x -> {
            throw new RuntimeException("" + x);
        });

        Stream.mergeInterleaved(a, b) //
                // .doOnError(e -> e.printStackTrace()) //
                .test() //
                .assertValues(1, 2) //
                .assertError(CompositeException.class) //
                .assertError(e -> {
                    CompositeException ex = (CompositeException) e;
                    Throwable exb = ex.getCause().getCause();
                    Throwable exa = exb.getCause();
                    System.out.println(exb.getMessage());
                    return exb.getMessage().equals("2") && exa.getMessage().equals("1");
                });
    }

    @Test
    public void testMax() {
        Stream.of(1, 5, 2) //
                .max(Comparator.naturalOrder()) //
                .test() //
                .assertValue(5);
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
                .take(5) //
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

    @Test(expected = IllegalStateException.class)
    public void testToMaybeFromTwo() {
        Stream.of(1, 2).maybe().test().assertValue(1);
    }

    public void testTo() {
        Stream.of(1, 2).to(stream -> stream.count()).test().assertValueOnly(1L);
    }
}
