package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.Test;

public class LinkedListTest {

    @Test
    public void test() {

        LinkedList<Integer> list = LinkedList.of(1, 2, 3);
        Iterator<Integer> it = list.iterator();
        assertEquals(1, (int) it.next());
        assertEquals(2, (int) it.next());
        assertEquals(3, (int) it.next());
    }

}
