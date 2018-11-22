package org.davidmoten.kool;

import java.io.IOException;

import org.junit.Test;

public class MaybeTesterTest {

    @Test(expected = AssertionError.class)
    public void testValueDoesNotExist() {
        Maybe.empty().test().assertValue(1);
    }
    
    @Test(expected = AssertionError.class)
    public void testWrongValue() {
        Maybe.of(1).test().assertValue(2);
    }
    
    @Test(expected = AssertionError.class)
    public void testValueExists() {
        Maybe.of(1).test().assertNoValue();
    }
    
    @Test(expected = AssertionError.class)
    public void testNoError() {
        Maybe.of(1).test().assertError(Exception.class);
    }
    
    @Test(expected = AssertionError.class)
    public void testErrorWrongClass() {
        Maybe.error(new IOException()).test().assertError(IllegalArgumentException.class);
    }
    
    @Test(expected = AssertionError.class)
    public void testErrorExists() {
        Maybe.error(new IOException()).test().assertNoError();
    }

    @Test(expected = AssertionError.class)
    public void testErrorMessageNoError() {
        Maybe.of(1).test().assertErrorMessage("boo");
    }
    
    @Test(expected = AssertionError.class)
    public void testErrorWrongMessage() {
        Maybe.error(new IOException()).test().assertErrorMessage("boo");
    }
    
}
