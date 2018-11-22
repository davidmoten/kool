package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Predicate;

public final class TakeWithPredicate<T> implements Stream<T> {

    private final Predicate<? super T> predicate;
    private final Stream<T> source;
    private final boolean until;

    public TakeWithPredicate(Predicate<? super T> predicate, Stream<T> source, boolean until) {
        this.predicate = predicate;
        this.source = source;
        this.until = until;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = source.iteratorChecked();

            T value;

            @Override
            public boolean hasNext() {
                loadNext();
                return value != null;
            }

            @Override
            public T next() {
                loadNext();
                if (value != null) {
                    T t = value;
                    value = null;
                    return t;
                } else {
                    throw new NoSuchElementException();
                }
            }

            private void loadNext() {
                if (value == null && it != null) {
                    if (it.hasNext()) {
                        T v = it.nextChecked();
                        boolean test = predicate.testUnchecked(v);
                        final boolean ok;
                        if (until) {
                            ok = !test;
                        } else {
                            ok = test;
                        }
                        if (ok) {
                            value = v;
                        } else {
                            it.dispose();
                            it = null;
                        }
                    } else {
                        it.dispose();
                        it = null;
                    }
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
