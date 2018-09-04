package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class Skip<T> implements Stream<T> {

    private final int count;
    private final Stream<T> source;

    public Skip(int count, Stream<T> source) {
        this.count = count;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = Preconditions.checkNotNull(source.iterator());
            int n = count;

            @Override
            public boolean hasNext() {
                skip();
                return it.hasNext();
            }

            @Override
            public T next() {
                skip();
                return it.next();
            }

            @Override
            public void dispose() {
                it.dispose();
            }

            private void skip() {
                while (n > 0 && it.hasNext()) {
                    it.next();
                    n--;
                }
            }

        };
    }

}
