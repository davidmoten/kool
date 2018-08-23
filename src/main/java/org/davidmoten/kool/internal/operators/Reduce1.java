package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

import org.davidmoten.kool.Optional;

public class Reduce1<T> implements Iterable<Optional<T>> {

    private final BiFunction<? super T, ? super T, ? extends T> reducer;
    private final Iterable<T> source;

    public Reduce1(BiFunction<? super T, ? super T, ? extends T> reducer, Iterable<T> source) {
        this.reducer = reducer;
        this.source = source;
    }

    @Override
    public Iterator<Optional<T>> iterator() {
        return new Iterator<Optional<T>>() {

            Iterator<T> it = source.iterator();
            T value = null;
            boolean finished;

            @Override
            public boolean hasNext() {
                calculate();
                return value != null;
            }

            @Override
            public Optional<T> next() {
                if (finished) {
                    throw new NoSuchElementException();
                }
                finished = true;
                calculate();
                T t = value;
                if (t != null) {
                    value = null;
                    return Optional.of(t);
                } else {
                    return Optional.empty();
                }
            }

            private void calculate() {
                if (value != null) {
                    return;
                } else {
                    T a, b;
                    if (it.hasNext()) {
                        a = it.next();
                    } else {
                        return;
                    }
                    if (it.hasNext()) {
                        b = it.next();
                    } else {
                        return;
                    }
                    T v = reducer.apply(a, b);
                    while (it.hasNext()) {
                        v = reducer.apply(v, it.next());
                    }
                    value = v;
                }
            }

        };
    }

}