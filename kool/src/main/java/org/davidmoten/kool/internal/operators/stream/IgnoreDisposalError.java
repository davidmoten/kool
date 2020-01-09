package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Plugins;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Consumer;

public final class IgnoreDisposalError<T> implements Stream<T> {

    private final Stream<T> stream;
    private final Consumer<? super Throwable> consumer;

    public IgnoreDisposalError(Stream<T> stream, Consumer<? super Throwable> consumer) {
        this.stream = stream;
        this.consumer = consumer;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = stream.iteratorNullChecked();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.nextNullChecked();
            }

            @Override
            public void dispose() {
                if (it != null) {
                    try {
                        it.dispose();
                    } catch (Throwable e) {
                        if (consumer != null) {
                            try {
                                consumer.accept(e);
                            } catch (Throwable e2) {
                                Plugins.onError(e2);
                            }
                        }
                    } finally {
                        it = null;
                    }
                }
            }

        };
    }

}
