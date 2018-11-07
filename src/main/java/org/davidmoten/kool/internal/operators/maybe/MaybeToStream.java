package org.davidmoten.kool.internal.operators.maybe;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class MaybeToStream<T> implements Stream<T> {

    private final Maybe<T> maybe;

    public MaybeToStream(Maybe<T> maybe) {
        this.maybe = maybe;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {
            Optional<T> value;
            boolean finished;

            @Override
            public boolean hasNext() {
                load();
                if (finished) {
                    return false;
                } else {
                    return value.isPresent();
                }
            }

            @Override
            public T next() {
                load();
                if (finished) {
                    throw new NoSuchElementException();
                } else if (value.isPresent()) {
                    finished = true;
                    T v = value.get();
                    value = null;
                    return v;
                } else {
                    value = null;
                    finished = true;
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void dispose() {
                // do nothing
            }

            private void load() {
                if (!finished && value == null) {
                    value = maybe.get();
                }
            }

        };
    }

}
