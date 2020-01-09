package org.davidmoten.kool.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class IterablesTest {

    @Test
    public void testFromArray() {
        Iterator<Integer> it = Iterables.fromArray(new Integer[] { 1, 2 }).iterator();
        assertTrue(it.hasNext());
        assertEquals(1, (int) it.next());
        assertTrue(it.hasNext());
        assertEquals(2, (int) it.next());
        assertFalse(it.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testFromArrayIteratorReadsTooFar() {
        Iterator<Integer> it = Iterables.fromArray(new Integer[] { 1 }).iterator();
        assertTrue(it.hasNext());
        assertEquals(1, (int) it.next());
        it.next();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testOfNoCopyIteratorReadsTooFar() {
        Iterator<Integer> it = Iterables.ofNoCopy(1).iterator();
        assertTrue(it.hasNext());
        assertEquals(1, (int) it.next());
        it.next();
    }
    

}
