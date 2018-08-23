package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.davidmoten.guavamini.Lists;

public class LazySeqTest {

    @Test
    public void testMapFilter() {
        assertEquals(1, (int) //
        LinkedList //
                .lazy() //
                .of(1, 2) //
                .map(x -> x + 1) //
                .filter(x -> x > 2) //
                .count());
    }
    
    @Test
    public void testPrepend() {
        assertEquals(Lists.newArrayList(0,1,2,3),  LinkedList.lazy().of(1,2,3).prepend(0).toJavaArrayList());
    }

}
