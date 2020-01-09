package org.davidmoten.kool.internal.operators.single;

import java.util.function.Consumer;

import org.davidmoten.kool.Single;

public final class SingleDoOnValue<T> implements Single<T> {

    private final Consumer<? super T> consumer;
    private final Single<T> single;

    public SingleDoOnValue(Consumer<? super T> consumer, Single<T> single) {
        this.consumer = consumer;
        this.single = single;
    }

    @Override
    public T get() {
        T v = single.get();
        consumer.accept(v);
        return v;
    }
}
