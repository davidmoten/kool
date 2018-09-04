package org.davidmoten.kool.internal.operators.stream;

import java.util.function.Supplier;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class Defer<T> implements Stream<T> {

    private final Supplier<? extends StreamIterable<? extends T>> supplier;

    public Defer(Supplier<? extends StreamIterable<? extends T>> supplier) {
        this.supplier = supplier;
    }

    @SuppressWarnings("unchecked")
    @Override
    public StreamIterator<T> iterator() {
        return Preconditions.checkNotNull((StreamIterator<T>) supplier.get().iterator());
    }

}
