package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.function.Predicate;
import org.davidmoten.kool.internal.util.Exceptions;

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

    public static <T> Stream<T> build(Stream<T> stream, Stream<Long> delays, int maxRetries,
            Predicate<? super Throwable> predicate) {
        return Stream.defer(() -> {
            int[] retryNumber = new int[1];
            StreamIterator<Long> delaysIt = delays == null ? null : delays.iteratorNullChecked();
            Function<Throwable, Single<?>> function = e -> {
                retryNumber[0]++;
                if (maxRetries > 0) {
                    if (retryNumber[0] > maxRetries) {
                        return Exceptions.rethrow(e);
                    }
                }
                if (predicate != null) {
                    if (!predicate.test(e)) {
                        return Exceptions.rethrow(e);
                    }
                }
                if (delaysIt != null) {
                    if (delaysIt.hasNext()) {
                        long ms = delaysIt.nextNullChecked();
                        return Single.timer(ms, TimeUnit.MILLISECONDS);
                    } else {
                        return Exceptions.rethrow(e);
                    }
                }
                return Single.of(1);
            };
            return new RetryWhen<T>(stream, function);
        });
    }

}
