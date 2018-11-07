package org.davidmoten.kool.internal.operators.single;

import java.util.function.Consumer;

import org.davidmoten.kool.Single;

public class SingleDoOnError<T> implements Single<T> {

    private Consumer<? super Throwable> consumer;
    private Single<T> single;

    public SingleDoOnError(Consumer<? super Throwable> consumer, Single<T> single) {
        this.consumer = consumer;
        this.single = single;
    }

    @Override
    public T get() {
        try {
            return single.get();
        } catch (Throwable e) {
            consumer.accept(e);
            throw e;
        }
    }

}
