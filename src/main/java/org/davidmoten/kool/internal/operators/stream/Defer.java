package org.davidmoten.kool.internal.operators.stream;

import java.util.concurrent.Callable;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.exceptions.UncheckedException;

import com.github.davidmoten.guavamini.Preconditions;

public final class Defer<T> implements Stream<T> {

    private final Callable<? extends Stream<? extends T>> provider;

    public Defer(Callable<? extends Stream<? extends T>> provider) {
        this.provider = provider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public StreamIterator<T> iterator() {
        try {
            return Preconditions.checkNotNull((StreamIterator<T>) provider.call().iterator());
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

}
