package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class Skip<T> implements Stream<T> {

    private final long count;
    private final Stream<T> source;

    public Skip(long count, Stream<T> source) {
        this.count = count;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = source.iteratorNullChecked();
            long n = count;

            @Override
            public boolean hasNext() {
                skip();
                return it.hasNext();
            }

            @Override
            public T next() {
                skip();
                return it.nextNullChecked();
            }

            @Override
            public void dispose() {
                it.dispose();
            }

            private void skip() {
                while (n > 0 && it.hasNext()) {
                    it.nextNullChecked();
                    n--;
                }
            }

        };
    }

}
