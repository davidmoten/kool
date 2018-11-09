package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
        .flatMap(x -> Stream.of(x,x)) //
        .test() //
        .assertValues(1,1);
    }
    
    @Test
    public void testMaybeFlatMapEmpty() {
        Maybe.<Integer>empty() //
        .flatMap(x -> Stream.of(x,x)) //
        .test() //
        .assertNoValues();
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
    
}