package org.davidmoten.kool;

import static org.junit.Assert.assertFalse;

import org.davidmoten.kool.function.Predicates;
import org.junit.Test;

public class PredicatesTest {
    
    @Test
    public void testNot() throws Exception {
        assertFalse(Predicates.not(x -> true).test("hello"));
    }

}
