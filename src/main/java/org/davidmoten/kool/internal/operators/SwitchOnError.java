package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.function.Function;

import org.davidmoten.kool.Stream;

public final class SwitchOnError<T> implements Stream<T> {

    private final Function<? super Throwable, ? extends Iterable<? extends T>> function;
    private final Stream<T> source;

    public SwitchOnError(Function<? super Throwable, ? extends Iterable<? extends T>> function, Stream<T> source) {
        this.function = function;
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Iterator<T> it = getIterator();
            boolean switched = false;

            @SuppressWarnings("unchecked")
            private Iterator<T> getIterator() {
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
                        it = (Iterator<T>) function.apply(e);
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
                        it = (Iterator<T>) function.apply(e);
                        return it.next();
                    }
                }
            }

        };
    }
    
}
