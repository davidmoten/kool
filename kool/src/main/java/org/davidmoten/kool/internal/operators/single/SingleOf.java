package org.davidmoten.kool.internal.operators.single;

import org.davidmoten.kool.Single;

import com.github.davidmoten.guavamini.Preconditions;

public final class SingleOf<T> implements Single<T> {

    private T value;

    public SingleOf(T value) {
        this.value = Preconditions.checkNotNull(value);
    }

    @Override
    public T get() {
        return value;
    }

}
