package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OptionalTest {
    
    @Test
    public void testToLinkedListEmpty() {
        assertTrue(Optional.empty().toLinkedList().isEmpty());
    }
    
    @Test
    public void testToLinkedListPresent() {
        assertEquals(1, (int) Optional.of(1).toLinkedList().first().get());
    }

}
