package org.davidmoten.kool.internal.operators.maybe;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.internal.util.Exceptions;

public class MaybeError<T> implements Maybe<T> {

    private final Callable<? extends Throwable> callable;

    public MaybeError(Callable<? extends Throwable> callable) {
        this.callable = callable;
    }

    @Override
    public Optional<T> get() {
        return Exceptions.rethrow(callable);
    }

}
