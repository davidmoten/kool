package org.davidmoten.kool.internal.operators;

import java.util.function.Consumer;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

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

            final StreamIterator<T> it = Preconditions.checkNotNull(source.iterator());

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                T t = Preconditions.checkNotNull(it.next());
                consumer.accept(t);
                return t;
            }

            @Override
            public void dispose() {
                it.dispose();
            }
        };
    }

}
