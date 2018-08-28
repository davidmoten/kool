package org.davidmoten.kool.internal.operators;

import java.util.NoSuchElementException;
import java.util.function.Function;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

public final class FlatMap<T, R> implements Stream<R> {

    private final Function<? super T, ? extends Stream<? extends R>> function;
    private final StreamIterable<T> source;

    public FlatMap(Function<? super T, ? extends Stream<? extends R>> function,
            StreamIterable<T> source) {
        this.function = function;
        this.source = source;
    }

    @Override
    public StreamIterator<R> iterator() {
        return new StreamIterator<R>() {

            final StreamIterator<T> a = source.iterator();
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
                            b = function.apply(a.next()).iterator();
                            if (b.hasNext()) {
                                r = b.next();
                                return;
                            } else {
                                b.cancel();
                                b = null;
                            }
                        } else {
                            return;
                        }
                    } else {
                        if (b.hasNext()) {
                            r = b.next();
                            return;
                        } else {
                            b.cancel();
                            b = null;
                        }
                    }
                }
            }

            @Override
            public void cancel() {
                if (a != null) {
                    a.cancel();
                }
                if (b != null) {
                    b.cancel();
                }
            }

        };
    }

}
