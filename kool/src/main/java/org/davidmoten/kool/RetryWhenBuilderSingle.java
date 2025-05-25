package org.davidmoten.kool;

import java.util.concurrent.TimeUnit;

import org.davidmoten.kool.function.Predicate;
import org.davidmoten.kool.internal.operators.stream.RetryWhen;

public final class RetryWhenBuilderSingle<T> {

    private final Single<T> single;
    private Stream<Long> delays;
    private int maxRetries;
    private Predicate<? super Throwable> predicate;

    public RetryWhenBuilderSingle(Single<T> single) {
        this.single = single;
    }

    public RetryWhenBuilderSingle<T> delay(long duration, TimeUnit unit) {
        this.delays = Single.of(unit.toMillis(duration)).repeat();
        return this;
    }

    public RetryWhenBuilderSingle<T> maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public RetryWhenBuilderSingle<T> delays(Stream<Long> delays, TimeUnit unit) {
        this.delays = delays.map(unit::toMillis);
        return this;
    }

    public RetryWhenBuilderSingle<T> isTrue(Predicate<? super Throwable> predicate) {
        this.predicate = predicate;
        return this;
    }

    public Single<T> build() {
        return RetryWhen.build(single.toStream(), delays, maxRetries, predicate).single();
    }

}
