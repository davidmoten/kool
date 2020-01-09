package org.davidmoten.kool.internal.operators.stream;

import java.util.concurrent.Callable;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.Exceptions;

public final class Defer<T> implements Stream<T> {

    private final Callable<? extends Stream<? extends T>> provider;

    public Defer(Callable<? extends Stream<? extends T>> provider) {
        this.provider = provider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public StreamIterator<T> iterator() {
        try {
            return (StreamIterator<T>) provider.call().iteratorNullChecked();
        } catch (Exception e) {
            return Exceptions.rethrow(e);
        }
    }

}
