package org.davidmoten.kool.internal.operators;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class RepeatElement<T> implements Stream<T> {

    private final T t;
    private final long count;

    public RepeatElement(T t, long count) {
        this.t = t;
        this.count = count;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            long i = 0;

            @Override
            public boolean hasNext() {
                return i < count;
            }

            @Override
            public T next() {
                if (i == count) {
                    throw new NoSuchElementException();
                } else {
                    i++;
                    return t;
                }

            }

            @Override
            public void dispose() {
                // do nothing
            }

        };
    }

}
