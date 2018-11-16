package org.davidmoten.kool.internal.operators.maybe;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.exceptions.UncheckedException;
import org.davidmoten.kool.internal.util.Exceptions;

public final class MaybeDefer<T> implements Maybe<T> {

    private final Callable<? extends Maybe<? extends T>> factory;

    public MaybeDefer(Callable<? extends Maybe<? extends T>> factory) {
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> get() {
        try {
            return (Optional<T>) factory.call().get();
        } catch (Throwable e) {
            return Exceptions.rethrow(e);
        }
    }

}
