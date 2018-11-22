package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Function;

import com.github.davidmoten.guavamini.Preconditions;

public final class SwitchOnError<T> implements Stream<T> {

    private final Function<? super Throwable, ? extends StreamIterable<? extends T>> function;
    private final Stream<T> source;

    public SwitchOnError(
            Function<? super Throwable, ? extends StreamIterable<? extends T>> function,
            Stream<T> source) {
        this.function = function;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = getIterator();
            boolean switched = false;

            @SuppressWarnings("unchecked")
            private StreamIterator<T> getIterator() {
                try {
                    return source.iteratorChecked();
                } catch (RuntimeException | Error e) {
                    switched = true;
                    return Preconditions
                            .checkNotNull(((Stream<T>) function.applyUnchecked(e)).iterator());
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean hasNext() {
                if (switched) {
                    return it.hasNext();
                } else {
                    try {
                        return it.hasNext();
                    } catch (Throwable e) {
                        switched = true;
                        it.dispose();
                        it = Preconditions
                                .checkNotNull((StreamIterator<T>) function.applyUnchecked(e));
                        return it.hasNext();
                    }
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public T next() {
                if (switched) {
                    return it.nextChecked();
                } else {
                    try {
                        return it.nextChecked();
                    } catch (Throwable e) {
                        switched = true;
                        it.dispose();
                        it = Preconditions
                                .checkNotNull((StreamIterator<T>) function.applyUnchecked(e));
                        return it.next();
                    }
                }
            }

            @Override
            public void dispose() {
                it.dispose();
            }

        };
    }

}
