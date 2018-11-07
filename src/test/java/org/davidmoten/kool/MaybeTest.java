package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

}
