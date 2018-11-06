package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public class ToSingle<T> implements Single<T> {

    private final Stream<T> stream;

    public ToSingle(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public T get() {
        StreamIterator<T> it = stream.iterator();
        try {
            if (it.hasNext()) {
                T v = it.next();
                if (it.hasNext()) {
                    throw new IllegalArgumentException("stream must only have one element but has more");
                } else {
                    return v;
                }
            } else {

                throw new NoSuchElementException();
            }
        } finally {
            it.dispose();
        }
    }

}
