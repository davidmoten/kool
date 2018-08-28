package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.function.Consumer;

import org.davidmoten.kool.Stream;

public final class OnValue<T> implements Stream<T> {

    private final Consumer<? super T> consumer;
    private final Iterable<T> source;

    public OnValue(Consumer<? super T> consumer, Iterable<T> source) {
        this.consumer = consumer;
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            final Iterator<T> it = source.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                T t = it.next();
                consumer.accept(t);
                return t;
            }
        };
    }

}
