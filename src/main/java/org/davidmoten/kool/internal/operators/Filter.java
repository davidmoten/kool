package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import org.davidmoten.kool.Stream;

public final class Filter<T> implements Stream<T> {

    private final Predicate<? super T> predicate;
    private final Iterable<? extends T> source;

    public Filter(Predicate<? super T> predicate, Iterable<? extends T> source) {
        this.predicate = predicate;
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Iterator<? extends T> it = source.iterator();
            T nextValue = null;

            @Override
            public boolean hasNext() {
                nextValue();
                return nextValue != null;
            }

            @Override
            public T next() {
                nextValue();
                if (nextValue != null) {
                     T t = nextValue;
                     nextValue = null;
                    return t;
                } else {
                    throw new NoSuchElementException();
                }
            }

            void nextValue() {
                while (true) {
                    if (nextValue != null) {
                        break;
                    } else if (!it.hasNext()) {
                        break;
                    } else {
                        T t = it.next();
                        if (predicate.test(t)) {
                            nextValue = t;
                            break;
                        }
                    }
                }
            }

        };
    }

}
