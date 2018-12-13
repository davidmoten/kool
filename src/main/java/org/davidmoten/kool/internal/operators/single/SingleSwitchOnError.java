package org.davidmoten.kool.internal.operators.single;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.function.Function;

import com.github.davidmoten.guavamini.Preconditions;

public final class SingleSwitchOnError<T> implements Single<T> {

    private final Single<T> single;
    private final Function<? super Throwable, ? extends Single<? extends T>> function;

    public SingleSwitchOnError(Single<T> single, Function<? super Throwable, ? extends Single<? extends T>> function) {
        Preconditions.checkNotNull(function, "switch function cannot be null");
        this.single = single;
        this.function = function;
    }

    @Override
    public T get() {
        try {
            return single.get();
        } catch (Throwable e) {
            return function.applyUnchecked(e).get();
        }
    }

}
