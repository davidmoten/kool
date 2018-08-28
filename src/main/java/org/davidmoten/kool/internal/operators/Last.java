package org.davidmoten.kool.internal.operators;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

public class Last<T> implements Stream<T> {

    private StreamIterable<T> source;

    public Last(StreamIterable<T> source) {
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = source.iterator();
            T t;

            @Override
            public boolean hasNext() {
                moveToLast();
                return it != null && t != null;
            }

            @Override
            public T next() {
                moveToLast();
                if (t == null) {
                    it.cancel();
                    throw new NoSuchElementException();
                } else {
                    it.cancel();
                    it = null;
                    return t;
                }
            }

            private void moveToLast() {
                if (it != null) {
                    while (it.hasNext()) {
                        t = it.next();
                    }
                }
            }

            @Override
            public void cancel() {
                if (it != null) {
                    it.cancel();
                }
            }
        };
    }

}
