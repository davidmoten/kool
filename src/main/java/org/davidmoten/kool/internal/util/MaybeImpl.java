package org.davidmoten.kool.internal.util;

import java.util.Optional;

import org.davidmoten.kool.Maybe;

public final class MaybeImpl<T> implements Maybe<T> {

    private final Optional<T> value;

    public MaybeImpl(Optional<T> value) {
        this.value = value;
    }

    @Override
    public Optional<T> get() {
        return value;
    }
    
    public static final class EmptyHolder {
        public final static Maybe<Object> INSTANCE = new MaybeImpl<Object>(Optional.empty());
    }

}
