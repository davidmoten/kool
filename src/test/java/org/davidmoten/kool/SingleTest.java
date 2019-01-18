package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class SingleTest {

    @Test
    public void testDoOnError() {
        RuntimeException error = new RuntimeException("boo");
        AtomicBoolean b = new AtomicBoolean();
        Single //
                .error(error) //
                .doOnError(e -> b.set(e == error)) //
                .test() //
                .assertError(RuntimeException.class);
        assertTrue(b.get());
    }

    @Test
    public void testDoOnValue() {
        AtomicBoolean b = new AtomicBoolean();
        Single.of(1).doOnValue(x -> b.set(x == 1)).forEach();
        assertTrue(b.get());
    }

    @Test
    public void testFromCallable() {
        Single.fromCallable(() -> 1).test().assertValue(1);
    }

    @Test
    public void testFromCallableReturnsNull() {
        Single.fromCallable(() -> null).test().assertError(NullPointerException.class);
    }

    @Test
    public void testFromCallableThrows() {
        RuntimeException error = new RuntimeException("boo");
        Single.fromCallable(() -> {
            throw error;
        }).test().assertError(RuntimeException.class).assertErrorMessage("boo");
    }

    @Test
    public void testTimer() {
        long[] time = new long[1];
        long t = System.currentTimeMillis();
        Single.timer(200, TimeUnit.MILLISECONDS) //
                .doOnValue(v -> time[0] = System.currentTimeMillis()) //
                .test() //
                .assertValue(1);
        assertTrue(time[0] >= t + 200);
    }

    @Test
    public void testSingleFlatMapMaybe() {
        Single.of(1).flatMapMaybe(x -> Maybe.of(2)).test().assertValue(2);
    }
    
    @Test
    public void testSwitchOnErrorWhenNoError() {
        Single.of(1).switchOnError(e -> Single.of(2)).test().assertValueOnly(1);
    }
    
    @Test
    public void testSwitchOnErrorWhenError() {
        Single.error(new RuntimeException("boo")).switchOnError(e -> Single.of(2)).test().assertValueOnly(2);
    }
    
    @Test
    public void testTo() {
        assertEquals(2, (int) Single.of(1).to(x -> 2));
    }
    
    @Test
    public void testGo() {
        AtomicBoolean done = new AtomicBoolean();
        Single.of(1).doOnValue(x -> done.set(true)).go();
        assertTrue(done.get());
    }
    
    @Test
    public void testStart() {
        AtomicBoolean done = new AtomicBoolean();
        Single.of(1).doOnValue(x -> done.set(true)).start();
        assertTrue(done.get());
    }
}
