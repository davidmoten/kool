package org.davidmoten.kool;

import java.util.concurrent.TimeUnit;

import org.davidmoten.kool.function.Predicate;
import org.davidmoten.kool.internal.operators.stream.RetryWhen;

public final class RetryWhenBuilderMaybe<T> {

    private final Maybe<T> maybe;
    private Stream<Long> delays;
    private int maxRetries;
    private Predicate<? super Throwable> predicate;

    public RetryWhenBuilderMaybe(Maybe<T> maybe) {
        this.maybe = maybe;
    }

    public RetryWhenBuilderMaybe<T> delay(long duration, TimeUnit unit) {
        this.delays = Single.of(unit.toMillis(duration)).repeat();
        return this;
    }

    public RetryWhenBuilderMaybe<T> maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public RetryWhenBuilderMaybe<T> delays(Stream<Long> delays, TimeUnit unit) {
        this.delays = delays.map(x -> unit.toMillis(x));
        return this;
    }

    public RetryWhenBuilderMaybe<T> isTrue(Predicate<? super Throwable> predicate) {
        this.predicate = predicate;
        return this;
    }

    public Maybe<T> build() {
        return RetryWhen.build(maybe.toStream(), delays, maxRetries, predicate).maybe();
    }

}
