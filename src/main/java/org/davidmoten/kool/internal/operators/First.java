package org.davidmoten.kool.internal.operators;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class First<T> implements Stream<T> {

    private final StreamIterable<T> source;

    public First(StreamIterable<T> source) {
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = Preconditions.checkNotNull(source.iterator());
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
                    cancel();
                    throw new NoSuchElementException();
                } else {
                    cancel();
                    it = null;
                    return value;
                }
            }

            private void loadNext() {
                if (value == null && it != null && it.hasNext()) {
                    value = it.next();
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
