package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.function.Function;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

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
                    return source.iterator();
                } catch (RuntimeException | Error e) {
                    switched = true;
                    return ((Stream<T>) function.apply(e)).iterator();
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
                    } catch (RuntimeException | Error e) {
                        switched = true;
                        it.cancel();
                        it = (StreamIterator<T>) function.apply(e);
                        return it.hasNext();
                    }
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public T next() {
                if (switched) {
                    return it.next();
                } else {
                    try {
                        return it.next();
                    } catch (RuntimeException | Error e) {
                        switched = true;
                        it.cancel();
                        it = (StreamIterator<T>) function.apply(e);
                        return it.next();
                    }
                }
            }

            @Override
            public void cancel() {
                it.cancel();
            }

        };
    }

}
