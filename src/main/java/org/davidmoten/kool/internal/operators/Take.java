package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

public final class Take<T> implements Stream<T> {

    private final long n;
    private final StreamIterable<T> source;

    public Take(long n, StreamIterable<T> source) {
        this.n = n;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            final StreamIterator<T> it = source.iterator();
            int count;

            @Override
            public boolean hasNext() {
                return count < n && it.hasNext();
            }

            @Override
            public T next() {
                if (count == n || !it.hasNext()) {
                    it.cancel();
                    throw new NoSuchElementException();
                } else {
                    count++;
                    return it.next();
                }
            }

            @Override
            public void cancel() {
                it.cancel();
            }

        };
    }

}
