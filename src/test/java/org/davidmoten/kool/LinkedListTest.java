package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

import com.github.davidmoten.guavamini.Lists;

public class LinkedListTest {

    @Test
    public void testIterator() {

        LinkedList<Integer> list = LinkedList.of(1, 2, 3);
        Iterator<Integer> it = list.iterator();
        assertEquals(1, (int) it.next());
        assertEquals(2, (int) it.next());
        assertTrue(it.hasNext());
        assertEquals(3, (int) it.next());
        assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail();
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testNilIsEmpty() {
        LinkedList<Integer> list = LinkedList.nil();
        assertTrue(list.isEmpty());
    }

    @Test
    public void testMapNil() {
        LinkedList<Integer> list = LinkedList.nil();
        assertTrue(list.map(x -> x + 1).isEmpty());
    }

    @Test
    public void testToJavaArrayList() {
        assertEquals(Lists.newArrayList(1, 2, 3), LinkedList.of(1, 2, 3).toJavaArrayList());
    }

    @Test
    public void testMap() {
        assertEquals(LinkedList.of(4, 5, 6), LinkedList.of(1, 2, 3).map(x -> x + 3));
    }

    @Test
    public void testReduce() {
        assertEquals(10, (int) LinkedList.of(1, 2, 3, 4).reduce(() -> 0, (a, b) -> a + b));
    }

    @Test
    public void testReduceNoFactory() {
        assertEquals(10, (int) LinkedList.of(1, 2, 3, 4).reduce((a, b) -> a + b));
    }

    @Test
    public void testFlatMap() {
        assertEquals(LinkedList.of(1, 1, 2, 2, 3, 3), LinkedList.of(1, 2, 3).flatMap(x -> LinkedList.of(x, x)));
    }

    @Test
    public void testForEachFromIterable() {
        LinkedList.of(1, 2, 3).forEach(System.out::println);
    }

}
