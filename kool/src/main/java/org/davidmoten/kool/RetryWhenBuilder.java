package org.davidmoten.kool;

import java.util.concurrent.TimeUnit;

import org.davidmoten.kool.function.Predicate;
import org.davidmoten.kool.internal.operators.stream.RetryWhen;

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
        this.delays = delays.map(unit::toMillis);
        return this;
    }

    public RetryWhenBuilder<T> isTrue(Predicate<? super Throwable> predicate) {
        this.predicate = predicate;
        return this;
    }

    public Stream<T> build() {
        return RetryWhen.build(stream, delays, maxRetries, predicate);
    }

}
