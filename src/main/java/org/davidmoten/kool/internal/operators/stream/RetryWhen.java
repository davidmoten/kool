package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Function;

public final class RetryWhen<T> implements Stream<T> {

    private final Stream<T> stream;
    private final Function<? super Throwable, ? extends Single<?>> function;

    public RetryWhen(Stream<T> stream, Function<? super Throwable, ? extends Single<?>> function) {
        this.stream = stream;
        this.function = function;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {
            StreamIterator<T> it;
            T next;
            boolean finished;

            @Override
            public boolean hasNext() {
                load();
                return next != null;
            }

            @Override
            public T next() {
                load();
                if (next == null) {
                    throw new NoSuchElementException();
                } else {
                    T v = next;
                    next = null;
                    return v;
                }
            }

            public void load() {
                if (!finished && next == null) {
                    while (true) {
                        try {
                            if (it == null) {
                                it = stream.iteratorNullChecked();
                            }
                            if (it.hasNext()) {
                                next = it.nextNullChecked();
                            } else {
                                finished = true;
                            }
                            break;
                        } catch (Throwable e) {
                            // will rethrow if no more retries
                            function.applyUnchecked(e).get();
                            try {
                                dispose();
                            } catch (Throwable t) {
                                function.applyUnchecked(e).get();
                            }
                        }
                    }
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    it = null;
                    next = null;
                }
            }

        };
    }

}
