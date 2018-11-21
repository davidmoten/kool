package org.davidmoten.kool.internal.operators.stream;

import java.util.Optional;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

public final class Last<T> implements Maybe<T> {

    private StreamIterable<T> source;

    public Last(StreamIterable<T> source) {
        this.source = source;
    }

    @Override
    public Optional<T> get() {
        StreamIterator<T> it = source.iteratorChecked();
        try {
            T t = null;
            while (it.hasNext()) {
                t = it.nextChecked();
            }
            return Optional.ofNullable(t);
        } finally {
            it.dispose();
        }
    }

}
