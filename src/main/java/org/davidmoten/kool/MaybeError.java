package org.davidmoten.kool;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.davidmoten.kool.internal.util.Exceptions;

import com.github.davidmoten.guavamini.Preconditions;

public class MaybeError<T> implements Maybe<T> {

    private final Callable<? extends Throwable> callable;

    public MaybeError(Callable<? extends Throwable> callable) {
        this.callable = callable;
    }

    @Override
    public Optional<T> get() {
        Throwable error;
        try {
            error = Preconditions.checkNotNull(callable.call());
        } catch (Throwable e) {
            return Exceptions.rethrow(e);
        }
        return Exceptions.rethrow(error);
    }

}
