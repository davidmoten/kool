package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.function.Consumer;

import org.davidmoten.kool.Stream;

public final class DoOnError<T> implements Stream<T> {

    private Consumer<? super Throwable> consumer;
    private Stream<T> source;

    public DoOnError(Consumer<? super Throwable> consumer, Stream<T> source) {
        this.consumer = consumer;
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Iterator<T> it = getIterator();

            @Override
            public boolean hasNext() {
                try {
                    return it.hasNext();
                } catch (RuntimeException | Error t) {
                    consumer.accept(t);
                    throw t;
                }
            }

            @Override
            public T next() {
                try {
                    return it.next();
                } catch (RuntimeException | Error t) {
                    consumer.accept(t);
                    throw t;
                }
            }
            
            private Iterator<T> getIterator() {
                try {
                    return source.iterator();
                } catch (RuntimeException | Error t) {
                    consumer.accept(t);
                    throw t;
                }
            }

        };
    }

}
