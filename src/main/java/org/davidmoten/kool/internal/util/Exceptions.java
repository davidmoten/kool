package org.davidmoten.kool.internal.util;

import java.util.concurrent.Callable;

import org.davidmoten.kool.exceptions.UncheckedException;

import com.github.davidmoten.guavamini.Preconditions;

public final class Exceptions {
    
    private Exceptions() {
        // prevent instantiation
    }

    public static <T> T rethrow(Throwable e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else if (e instanceof Error) {
            throw (Error) e;
        } else {
            throw new UncheckedException(e);
        }
    }

    public static <T> T rethrow(Callable<? extends Throwable> callable) {
        try {
            return Exceptions.rethrow(Preconditions.checkNotNull(callable.call()));
        } catch (Throwable e) {
            return Exceptions.rethrow(e);
        }
    }

}
