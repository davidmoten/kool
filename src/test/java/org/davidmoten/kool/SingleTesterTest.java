package org.davidmoten.kool;

import org.junit.Test;

public class SingleTesterTest {
    
    @Test(expected=AssertionError.class)
    public void testNotValue() {
        Single.of(1).test().assertValue(2);
    }

}
