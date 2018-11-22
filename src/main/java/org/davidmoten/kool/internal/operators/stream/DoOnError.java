package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Consumer;

public final class DoOnError<T> implements Stream<T> {

    private Consumer<? super Throwable> consumer;
    private Stream<T> source;

    public DoOnError(Consumer<? super Throwable> consumer, Stream<T> source) {
        this.consumer = consumer;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = getIterator();

            @Override
            public boolean hasNext() {
                try {
                    return it.hasNext();
                } catch (RuntimeException | Error t) {
                    
                    throw t;
                }
            }

            @Override
            public T next() {
                try {
                    return it.nextChecked();
                } catch (RuntimeException | Error t) {
                    consumer.acceptUnchecked(t);
                    throw t;
                }
            }

            private StreamIterator<T> getIterator() {
                try {
                    return source.iteratorChecked();
                } catch (RuntimeException | Error t) {
                    consumer.acceptUnchecked(t);
                    throw t;
                }
            }

            @Override
            public void dispose() {
                it.dispose();
            }

        };
    }

}
