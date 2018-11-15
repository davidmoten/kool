package org.davidmoten.kool.internal.util;

import org.davidmoten.kool.exceptions.UncheckedException;

public final class Exceptions {
    
    public static <T> T rethrow(Throwable e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else if (e instanceof Error) {
            throw (Error) e;
        } else {
            throw new UncheckedException(e);
        }
    }

}
