package org.davidmoten.kool.internal.operators.maybe;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.Single;

public final class MaybeOrElse<T> implements Single<T> {

    private final Maybe<T> maybe;
    private final T value;

    public MaybeOrElse(Maybe<T> maybe, T value) {
        this.maybe = maybe;
        this.value = value;
    }

    @Override
    public T get() {
        return maybe.get().orElse(value);
    }

}
