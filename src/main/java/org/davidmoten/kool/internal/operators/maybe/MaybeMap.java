package org.davidmoten.kool.internal.operators.maybe;

import java.util.Optional;
import java.util.function.Function;

import org.davidmoten.kool.Maybe;

public final class MaybeMap<T, R> implements Maybe<R> {

    private final Maybe<T> maybe;
    private final Function<? super T, ? extends R> mapper;

    public MaybeMap(Maybe<T> maybe, Function<? super T, ? extends R> mapper) {
        this.maybe = maybe;
        this.mapper = mapper;
    }

    @Override
    public Optional<R> get() {
        Optional<T> v = maybe.get();
        if (v.isPresent()) {
            return Optional.of(mapper.apply(v.get()));
        } else {
            return Optional.empty();
        }
    }

}
