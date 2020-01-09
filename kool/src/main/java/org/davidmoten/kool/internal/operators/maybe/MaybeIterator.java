package org.davidmoten.kool.internal.operators.maybe;

import java.util.Optional;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.StreamIterator;

public final class MaybeIterator<T> implements StreamIterator<T> {

    private final Maybe<T> maybe;
    private Optional<T> value;
    private boolean finished;

    public MaybeIterator(Maybe<T> maybe) {
        this.maybe = maybe;
    }

    @Override
    public boolean hasNext() {
        load();
        return !finished && value.isPresent();
    }

    @Override
    public T next() {
        load();
        try {
            if (finished) {
                throw new RuntimeException("stream is finished");
            } else {
                finished = true;
                return value.get();
            }
        } finally {
            dispose();
        }
    }

    @Override
    public void dispose() {
        finished = true;
        value = null;
    }

    private void load() {
        if (!finished && value == null) {
            value = maybe.get();
        }
    }

}
