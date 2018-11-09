package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IndexedTest {

    private static final Indexed<String> v = Indexed.create("a", 123);

    @Test
    public void testFields() {
        assertEquals("a", v.value());
        assertEquals(123, v.index());
        assertEquals(4871, v.hashCode());
        assertEquals("Indexed[value=a, index=123]", v.toString());
    }

    @Test(expected = NullPointerException.class)
    public void testValueCantBeNull() {
        Indexed.create(null, 123);
    }

    @Test
    public void testEqualsItself() {
        assertTrue(v.equals(v));
    }

    @Test
    public void testNotEqualToNull() {
        assertFalse(v.equals(null));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testNotEqualToDifferentType() {
        assertFalse(v.equals("abc"));
    }
    
    @Test
    public void testEqualsClone() {
        assertTrue(v.equals(Indexed.create("a", 123)));
    }
    
    @Test
    public void testNotEqualsDifferentValue() {
        assertFalse(v.equals(Indexed.create("b", 123)));
    }
    
    @Test
    public void testNotEqualsDifferentIndex() {
        assertFalse(v.equals(Indexed.create("a", 1)));
    }


}
