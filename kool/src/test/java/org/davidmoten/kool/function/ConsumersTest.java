package org.davidmoten.kool.function;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class ConsumersTest {
    
    @Test
    public void isUtilClass() {
        Asserts.assertIsUtilityClass(Consumers.class);
    }
    
    @Test
    public void testDoNothing() throws Exception {
        Consumers.doNothing().accept(new Object());
    }

}
