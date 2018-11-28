package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Function;

public final class FlatMap<T, R> implements Stream<R> {

    private final Function<? super T, ? extends StreamIterable<? extends R>> function;
    private final StreamIterable<T> source;

    public FlatMap(Function<? super T, ? extends StreamIterable<? extends R>> function, StreamIterable<T> source) {
        this.function = function;
        this.source = source;
    }

    @Override
    public StreamIterator<R> iterator() {
        return new StreamIterator<R>() {

            final StreamIterator<T> a = source.iteratorNullChecked();
            StreamIterator<? extends R> b;
            R r;

            @Override
            public boolean hasNext() {
                loadNext();
                return r != null;
            }

            @Override
            public R next() {
                loadNext();
                if (r == null) {
                    throw new NoSuchElementException();
                } else {
                    R r2 = r;
                    r = null;
                    return r2;
                }
            }

            private void loadNext() {
                if (r != null) {
                    return;
                }
                while (true) {
                    if (b == null) {
                        if (a.hasNext()) {
                            b = function.applyUnchecked(a.nextNullChecked()).iteratorNullChecked();
                        } else {
                            return;
                        }
                    }
                    if (b.hasNext()) {
                        r = b.nextNullChecked();
                        return;
                    } else {
                        b.dispose();
                        b = null;
                    }
                }
            }

            @Override
            public void dispose() {
                if (a != null) {
                    a.dispose();
                }
                if (b != null) {
                    b.dispose();
                }
            }

        };
    }

}
