package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.davidmoten.kool.exceptions.TestRuntimeException;
import org.davidmoten.kool.exceptions.UncheckedException;
import org.junit.Assert;
import org.junit.Test;

public final class MaybeTest {

    @Test
    public void testMaybeOf() {
        assertEquals(1, (int) Maybe.of(1).get().get());
    }

    @Test
    public void testMaybeEmpty() {
        assertFalse(Maybe.empty().get().isPresent());
    }

    @Test
    public void testMaybeEmptyIsSingleton() {
        assertTrue(Maybe.empty() == Maybe.empty());
    }

    @Test
    public void testMaybeFlatMap() {
        Maybe.of(1) //
                .flatMap(x -> Stream.of(x, x)) //
                .test() //
                .assertValues(1, 1);
    }

    @Test
    public void testMaybeFlatMapEmpty() {
        Maybe.<Integer>empty() //
                .flatMap(x -> Stream.of(x, x)) //
                .test() //
                .assertNoValues();
    }

    @Test
    public void testMaybeFlatMapMaybe() {
        Maybe.of(1).flatMapMaybe(x -> Maybe.of(x + 1)).test().assertValue(2);
    }

    @Test
    public void testMaybeMap() {
        Maybe.of(3).map(x -> x + 1).test().assertValue(4);
    }

    @Test
    public void testMaybeMapEmpty() {
        Maybe.<Integer>empty().map(x -> x + 1).test().assertNoValue();
    }

    @Test
    public void testMaybeDoOnValue() {
        AtomicInteger b = new AtomicInteger();
        Maybe.of(3).doOnValue(x -> b.set(x)).test().assertValue(3);
        assertEquals(3, b.get());
    }

    @Test
    public void testMaybeDoOnValueEmpty() {
        AtomicInteger b = new AtomicInteger();
        Maybe.<Integer>empty().doOnValue(x -> b.set(x)).test().assertNoValue();
        assertEquals(0, b.get());
    }

    @Test
    public void testMaybeDoOnEmptyWhenNotEmpty() {
        AtomicBoolean b = new AtomicBoolean();
        Maybe.of(3).doOnEmpty(() -> b.set(true)).test().assertValue(3);
        assertFalse(b.get());
    }

    @Test
    public void testMaybeDoOnEmptyWhenEmpty() {
        AtomicBoolean b = new AtomicBoolean();
        Maybe.<Integer>empty().doOnEmpty(() -> b.set(true)).test().assertNoValue();
        assertTrue(b.get());
    }

    @Test
    public void testMaybeOrElseWhenNotPresent() {
        Maybe.<Integer>empty().orElse(2).test().assertValue(2);
    }

    @Test
    public void testMaybeOrElseWhenPresent() {
        Maybe.of(1).orElse(2).test().assertValue(1);
    }

    @Test(expected = UncheckedException.class)
    public void testMaybeError() {
        Maybe.error(new IOException("boo")).get();
    }

    @Test
    public void testMaybeToStream() {
        Maybe.of(1).toStream().test().assertValuesOnly(1);
    }

    @Test
    public void testMaybeToStreamFromEmpty() {
        Maybe.empty().toStream().test().assertNoValuesOnly();
    }

    @Test
    public void testMaybeIsPresent() {
        Maybe.of(1).isPresent().test().assertValueOnly(true);
    }

    @Test
    public void testMaybeIsPresentWhenEmpty() {
        Maybe.empty().isPresent().test().assertValueOnly(false);
    }

    @Test
    public void testFromCallable() {
        Maybe.fromCallable(() -> 1).test().assertValue(1);
    }

    @Test
    public void testFromCallableReturnsEmpty() {
        Maybe.fromCallable(() -> 1).test().assertValue(1);
    }

    @Test
    public void testFromCallableReturnsNull() {
        Maybe.fromCallable(() -> null).test().assertError(NullPointerException.class);
    }

    @Test
    public void testFromCallableNullableReturnsNull() {
        Maybe.fromCallableNullable(() -> null).test().assertNoValue();
    }

    @Test
    public void testDoOnError() {
        AtomicReference<String> b = new AtomicReference<>();
        Maybe.error(new RuntimeException("boo")).doOnError(e -> b.set(e.getMessage())) //
                .switchOnError(e -> Maybe.empty()) //
                .forEach();
        assertEquals("boo", b.get());
    }

    @Test
    public void testDefer() {
        Maybe.defer(() -> Maybe.of(1)).test().assertValue(1);
    }

    @Test
    public void testMaybePresent() {
        Maybe.of(1).test().assertPresent();
    }

    @Test(expected = AssertionError.class)
    public void testMaybeNotPresent() {
        Maybe.empty().test().assertPresent();
    }

    @Test
    public void testFromOptional() {
        Maybe.fromOptional(Optional.of(1)).test().assertValue(1);
    }

    @Test
    public void testFromOptionalEmpty() {
        Maybe.fromOptional(Optional.empty()).test().assertNoValue();
    }

    @Test
    public void testMaybeIterator() {
        StreamIterator<Integer> it = Maybe.of(1).iterator();
        assertTrue(it.hasNext());
        assertEquals(1, (int) it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testMaybeEmptyIterator() {
        StreamIterator<Integer> it = Maybe.<Integer>empty().iterator();
        assertFalse(it.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testMaybeEmptyIteratorThrowsNoSuchElementException() {
        StreamIterator<Integer> it = Maybe.<Integer>empty().iterator();
        it.next();
    }

    @Test
    public void testMaybeOfNull() {
        Maybe.ofNullable(null).test().assertNoValue();
    }

    @Test
    public void testMaybeOfNonNull() {
        Maybe.ofNullable(1).test().assertValue(1);
    }

    @Test
    public void testRetryWhen() {
        AtomicInteger count = new AtomicInteger();
        Maybe.error(new TestRuntimeException()) //
                .doOnError(e -> count.incrementAndGet()) //
                .retryWhen() //
                .maxRetries(3) //
                .build() //
                .test() //
                .assertError(TestRuntimeException.class);
        assertEquals(4, count.get());
    }

    @Test
    public void testRetryWhenDelays() {
        AtomicInteger count = new AtomicInteger();
        Maybe.error(new TestRuntimeException()) //
                .doOnError(e -> count.incrementAndGet()) //
                .retryWhen() //
                .maxRetries(3) //
                .delay(1, TimeUnit.MILLISECONDS) //
                .build() //
                .test() //
                .assertError(TestRuntimeException.class);
        assertEquals(4, count.get());
    }

    @Test
    public void testRetryWhenDelaysStream() {
        AtomicInteger count = new AtomicInteger();
        Maybe.error(new TestRuntimeException()) //
                .doOnError(e -> count.incrementAndGet()) //
                .retryWhen() //
                .maxRetries(3) //
                .delays(Stream.of(1L).repeat(), TimeUnit.MILLISECONDS) //
                .build() //
                .test() //
                .assertError(TestRuntimeException.class);
        assertEquals(4, count.get());
    }

    @Test
    public void testRetryWhenWithPredicate() {
        AtomicInteger count = new AtomicInteger();
        Maybe.error(new TestRuntimeException()) //
                .doOnError(e -> count.incrementAndGet()) //
                .retryWhen() //
                .isTrue(e -> count.get() <= 3) //
                .build() //
                .test() //
                .assertError(TestRuntimeException.class);
        assertEquals(4, count.get());
    }

    @Test
    public void testTo() {
        assertEquals(2, (int) Maybe.of(1).to(x -> 2));
    }

    @Test
    public void testStart() {
        AtomicBoolean b = new AtomicBoolean();
        Maybe.of(1).doOnValue(x -> b.set(true)).start();
        assertTrue(b.get());
    }

    @Test
    public void testGo() {
        AtomicBoolean b = new AtomicBoolean();
        Maybe.of(1).doOnValue(x -> b.set(true)).go();
        assertTrue(b.get());
    }

    @Test
    public void testMaybeToStreamIterator() {
        StreamIterator<Integer> it = Maybe.of(1).iterator();
        assertTrue(it.hasNext());
        assertEquals(1, (int) it.next());
    }

    @Test
    public void testMaybeFilter() {
        assertEquals(1, (int) Maybe.of(1).filter(x -> x > 0).get().get());
    }

    @Test
    public void testMaybeFilterFails() {
        assertFalse(Maybe.of(1).filter(x -> x > 1).get().isPresent());
    }

    @Test
    public void testMaybeFilterThrows() {
        try {
            Maybe.of(1).filter(x -> {
                throw new RuntimeException("boo");
            }).get();
            Assert.fail();
        } catch (RuntimeException e) {
            assertEquals("boo", e.getMessage());
        }
    }

}
