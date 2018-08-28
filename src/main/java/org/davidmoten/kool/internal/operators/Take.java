package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;

public class Take<T> implements Stream<T> {

    private final long n;
    private final Iterable<T> source;

    public Take(long n, Iterable<T> source) {
        this.n = n;
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Iterator<T> it = source.iterator();
            int count;

            @Override
            public boolean hasNext() {
                return count < n && it.hasNext();
            }

            @Override
            public T next() {
                if (count == n || !it.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    count++;
                    return it.next();
                }
            }

        };
    }

}
