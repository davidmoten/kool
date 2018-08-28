package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;

public final class First<T> implements Stream<T> {

    private final Iterable<T> source;

    public First(Iterable<T> source) {
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Iterator<T> it = source.iterator();
            T value;

            @Override
            public boolean hasNext() {
                loadNext();
                return value != null;
            }

            @Override
            public T next() {
                loadNext();
                if (value == null || it == null) {
                    throw new NoSuchElementException();
                } else {
                    it = null;
                    return value;
                }
            }

            private void loadNext() {
                if (value == null && it != null && it.hasNext()) {
                    value = it.next();
                }
            }

        };
    }

}
