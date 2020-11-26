package org.davidmoten.kool.internal.util;

import java.util.Iterator;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class StreamImpl<T> implements Stream<T> {

    private final Iterable<? extends T> source;

    public StreamImpl(Iterable<? extends T> source) {
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            Iterator<? extends T> it = source.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }

            @Override
            public void dispose() {
                // do nothing
            }

        };
    }

}
