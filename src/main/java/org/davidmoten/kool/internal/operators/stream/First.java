package org.davidmoten.kool.internal.operators.stream;

import java.util.Optional;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

public final class First<T> implements Maybe<T> {

    private final StreamIterable<T> stream;

    public First(StreamIterable<T> stream) {
        this.stream = stream;
    }

    @Override
    public Optional<T> get() {
        StreamIterator<T> it = stream.iteratorNullChecked();
        try {
            if (it.hasNext()) {
                return Optional.of(it.nextNullChecked());
            } else {
                return Optional.empty();
            }
        } finally {
            it.dispose();
        }
    }

}
