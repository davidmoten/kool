package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

public final class PrependOne<T> implements Stream<T> {

    private final T value;
    private final StreamIterable<T> source;

    public PrependOne(T value, StreamIterable<T> source) {
        this.value = value;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {
            final StreamIterator<T> it = source.iteratorNullChecked();
            T value = PrependOne.this.value;

            @Override
            public boolean hasNext() {
                if (value != null) {
                    return true;
                } else {
                    return it.hasNext();
                }
            }

            @Override
            public T next() {
                T t = value;
                if (t != null) {
                    value = null;
                    return t;
                } else {
                    return it.nextNullChecked();
                }
            }

            @Override
            public void dispose() {
                it.dispose();
            }

        };
    }

}
