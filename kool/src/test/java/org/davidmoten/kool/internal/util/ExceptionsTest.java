package org.davidmoten.kool.internal.util;

import org.davidmoten.kool.exceptions.TestException;
import org.davidmoten.kool.exceptions.TestRuntimeException;
import org.davidmoten.kool.exceptions.UncheckedException;
import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class ExceptionsTest {

    @Test
    public void testIsUtilClass() {
        Asserts.assertIsUtilityClass(Exceptions.class);
    }

    @Test(expected = OutOfMemoryError.class)
    public void testRethrowError() {
        Exceptions.rethrow(new OutOfMemoryError());
    }

    @Test(expected = TestRuntimeException.class)
    public void testCallableRethrows() {
        Exceptions.rethrow(() -> new TestRuntimeException());
    }
    
    @Test(expected = UncheckedException.class)
    public void testCallableRethrowsChecked() {
        Exceptions.rethrow(() -> new TestException());
    }

}
