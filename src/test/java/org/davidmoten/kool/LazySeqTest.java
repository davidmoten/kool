package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LazySeqTest {

    @Test
    public void test() {
        assertEquals(1, (int) //
        LinkedList //
                .lazy() //
                .of(1, 2) //
                .map(x -> x + 1) //
                .filter(x -> x > 2) //
                .count());
    }

}
