package org.davidmoten.kool;

import java.util.concurrent.Callable;

import org.davidmoten.kool.exceptions.UncheckedException;

import com.github.davidmoten.guavamini.Preconditions;

public final class SingleFromCallable<T> implements Single<T> {

    private final Callable<? extends T> callable;

    public SingleFromCallable(Callable<? extends T> callable) {
        this.callable = callable;
    }

    @Override
    public T get() {
        try {
            return Preconditions.checkNotNull(callable.call(), "callable returned null!");
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

}
