package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class Repeat<T> implements Stream<T> {

    private final long count;
    private final Stream<T> source;

    public Repeat(long count, Stream<T> source) {
        this.count = count;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = Preconditions.checkNotNull(source.iterator());
            long i = 0;

            @Override
            public boolean hasNext() {
                if (it == null) {
                    return false;
                }
                while (!it.hasNext()) {
                    i += 1;
                    if (i == count) {
                        return false;
                    }
                    it = Preconditions.checkNotNull(source.iterator());
                }
                return true;
            }

            @Override
            public T next() {
                if (it == null) {
                    throw new NoSuchElementException();
                } else {
                    return it.next();
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    it = null;
                }
            }

        };
    }

}
