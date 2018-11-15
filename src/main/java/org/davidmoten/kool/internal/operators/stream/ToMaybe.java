package org.davidmoten.kool.internal.operators.stream;

import java.util.Optional;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public class ToMaybe<T> implements Maybe<T> {

    private final Stream<T> stream;

    public ToMaybe(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public Optional<T> get() {
        StreamIterator<T> it = Preconditions.checkNotNull(stream.iterator());
        try {
            if (!it.hasNext()) {
                return Optional.empty();
            } else {
                T v = Preconditions.checkNotNull(it.next());
                if (it.hasNext()) {
                    throw new IllegalStateException("stream has more than one element so cannot convert to Maybe");
                } else {
                    return Optional.of(v);
                }
            }
        } finally {
            it.dispose();
        }
    }

}
