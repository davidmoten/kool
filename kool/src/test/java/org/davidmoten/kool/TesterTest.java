package org.davidmoten.kool;

import java.io.IOException;

import org.davidmoten.kool.function.Predicates;
import org.junit.Test;

public class TesterTest {

    @Test(expected = AssertionError.class)
    public void testValueDoesNotExist() {
        Stream.empty().test().assertValues(1);
    }


    @Test(expected = AssertionError.class)
    public void testNoValues() {
        Stream.of(1).test().assertNoValues();
    }

    @Test(expected = AssertionError.class)
    public void testNoError() {
        Stream.error(new RuntimeException()).test().assertNoError();
    }
    
    @Test(expected = AssertionError.class)
    public void testErrorWrongClass() {
        Stream.error(new RuntimeException()).test().assertError(IOException.class);
    }

    @Test(expected = AssertionError.class)
    public void testErrorNotExist() {
        Stream.of(1).test().assertError(IOException.class);
    }
    
    @Test(expected = AssertionError.class)
    public void testPredicateNoError() {
        Stream.of(1).test().assertError(Predicates.alwaysTrue());
    }
    
    @Test(expected = AssertionError.class)
    public void testPredicateFalse() {
        Stream.error(new RuntimeException()).test().assertError(Predicates.alwaysFalse());
    }
    
}
