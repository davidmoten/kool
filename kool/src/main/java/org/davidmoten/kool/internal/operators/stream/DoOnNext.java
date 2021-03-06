package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Consumer;

public final class DoOnNext<T> implements Stream<T> {

    private final Consumer<? super T> consumer;
    private final StreamIterable<T> source;

    public DoOnNext(Consumer<? super T> consumer, StreamIterable<T> source) {
        this.consumer = consumer;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            final StreamIterator<T> it = source.iteratorNullChecked();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                T t = it.nextNullChecked();
                consumer.acceptUnchecked(t);
                return t;
            }

            @Override
            public void dispose() {
                it.dispose();
            }
        };
    }

}
