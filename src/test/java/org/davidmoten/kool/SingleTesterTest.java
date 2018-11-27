package org.davidmoten.kool;

import java.io.IOException;

import org.junit.Test;

public class SingleTesterTest {
    
    @Test(expected=AssertionError.class)
    public void testNotValue() {
        Single.of(1).test().assertValue(2);
    }
    
    @Test(expected=AssertionError.class)
    public void testNoError() {
        Single.error(new RuntimeException("boo")).test().assertNoError();
    }
    
    @Test(expected=AssertionError.class)
    public void testClassErrorIsNot() {
        Single.error(new RuntimeException("boo")).test().assertError(IOException.class);
    }
    
    @Test
    public void testClassErrorIs() {
        Single.error(new RuntimeException("boo")).test().assertError(RuntimeException.class);
    }
    
    @Test(expected=NullPointerException.class)
    public void testErrorClassNotNull() {
        Single.error(new RuntimeException("boo")).test().assertError(null);
    }

    @Test(expected=AssertionError.class)
    public void testErrorMessageIsNot() {
        Single.error(new RuntimeException("boo")).test().assertErrorMessage("abc");
    }
    
    @Test
    public void testErrorMessageIs() {
        Single.error(new RuntimeException("boo")).test().assertErrorMessage("boo");
    }
    
}
