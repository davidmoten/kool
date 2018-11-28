package org.davidmoten.kool;

import java.util.NoSuchElementException;

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
            StreamIterator<T> it = stream.iteratorChecked();
            T next;

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
                if (it != null && next == null) {
                    while (true) {
                        try {
                            if (it.hasNext()) {
                                next = it.nextChecked();
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
                            it = stream.iteratorChecked();
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
