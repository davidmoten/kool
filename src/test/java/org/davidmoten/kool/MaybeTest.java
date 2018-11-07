package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MaybeTest {
    
    @Test
    public void testToLinkedListEmpty() {
        assertTrue(Maybe.empty().toStream().isEmpty().first().get());
    }
    
    @Test
    public void testToLinkedListPresent() {
        assertEquals(1, (int) Maybe.of(1).toStream().first().get());
    }
    
}
