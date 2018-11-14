package org.davidmoten.kool.internal.operators.maybe;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.exceptions.UncheckedException;

public final class MaybeFromCallable<T> implements Maybe<T> {

    private final Callable<T> callable;
    private final boolean nullable;

    public MaybeFromCallable(Callable<T> callable, boolean nullable) {
        this.callable = callable;
        this.nullable = nullable;
    }

    @Override
    public Optional<T> get() {
        T v = null;
        try {
            v = callable.call();
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
        if (nullable) {
            return Optional.ofNullable(v);
        } else if (v == null){
            throw new NullPointerException("callable returned null!");
        } else {
            return Optional.of(v);
        }
    }

}
