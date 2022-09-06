package org.davidmoten.kool.internal.operators.single;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class SingleToStream<T> implements Stream<T> {

    private final Single<T> single;

    public SingleToStream(Single<T> single) {
        this.single = single;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {
            T value;
            boolean finished;

            @Override
            public boolean hasNext() {
                load();
                return !finished;
            }

            private void load() {
                if (!finished && value == null) {
                    value = single.get();
                }
            }

            @Override
            public T next() {
                load();
                if (finished) {
                    throw new NoSuchElementException();
                } else {
                    finished = true;
                    T v = value;
                    // clear for gc
                    value = null;
                    return v;
                }
            }

            @Override
            public void dispose() {
                // do nothing
            }

        };
    }
}
