package org.davidmoten.kool;

import java.util.concurrent.atomic.AtomicBoolean;

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
    }

}
