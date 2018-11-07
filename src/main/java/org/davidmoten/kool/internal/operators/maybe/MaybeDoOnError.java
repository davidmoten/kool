package org.davidmoten.kool.internal.operators.maybe;

import java.util.Optional;
import java.util.function.Consumer;

import org.davidmoten.kool.Maybe;

public final class MaybeDoOnError<T> implements Maybe<T> {

    private Consumer<? super Throwable> consumer;
    private Maybe<T> maybe;

    public MaybeDoOnError(Consumer<? super Throwable> consumer, Maybe<T> maybe) {
        this.consumer = consumer;
        this.maybe = maybe;
    }

    @Override
    public Optional<T> get() {
        try {
            return maybe.get();
        } catch (Throwable e) {
            consumer.accept(e);
            throw e;
        }
    }

}
