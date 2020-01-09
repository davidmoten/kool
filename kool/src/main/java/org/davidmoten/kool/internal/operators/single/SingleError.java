package org.davidmoten.kool.internal.operators.single;

import java.util.concurrent.Callable;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.internal.util.Exceptions;

public final class SingleError<T> implements Single<T> {

    private final Callable<? extends Throwable> callable;

    public SingleError(Callable<? extends Throwable> callable) {
        this.callable = callable;
    }

    @Override
    public T get() {
        return Exceptions.rethrow(callable);
    }

}
