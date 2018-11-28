package org.davidmoten.kool;

import java.util.concurrent.TimeUnit;

import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.function.Predicate;
import org.davidmoten.kool.internal.operators.stream.RetryWhen;
import org.davidmoten.kool.internal.util.Exceptions;

public final class RetryWhenBuilder<T> {

    private final Stream<T> stream;
    private Stream<Long> delays;
    private int maxRetries;
    private Predicate<? super Throwable> predicate;

    public RetryWhenBuilder(Stream<T> stream) {
        this.stream = stream;
    }

    public RetryWhenBuilder<T> delay(long duration, TimeUnit unit) {
        this.delays = Single.of(unit.toMillis(duration)).repeat();
        return this;
    }

    public RetryWhenBuilder<T> maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public RetryWhenBuilder<T> delays(Stream<Long> delays, TimeUnit unit) {
        this.delays = delays.map(x -> unit.toMillis(x));
        return this;
    }

    public RetryWhenBuilder<T> isTrue(Predicate<? super Throwable> predicate) {
        this.predicate = predicate;
        return this;
    }

    public Stream<T> build() {
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
