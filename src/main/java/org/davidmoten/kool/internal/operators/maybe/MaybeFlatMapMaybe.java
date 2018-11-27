package org.davidmoten.kool.internal.operators.maybe;

import java.util.Optional;
import java.util.function.Function;

import org.davidmoten.kool.Maybe;

import com.github.davidmoten.guavamini.Preconditions;

public final class MaybeFlatMapMaybe<T, R> implements Maybe<R> {

    private final Maybe<T> maybe;
    private final Function<? super T, ? extends Maybe<? extends R>> mapper;

    public MaybeFlatMapMaybe(Maybe<T> maybe, Function<? super T, ? extends Maybe<? extends R>> mapper) {
        this.maybe = maybe;
        this.mapper = mapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<R> get() {
        Optional<T> v = maybe.get();
        if (v.isPresent()) {
            return (Optional<R>) Preconditions.checkNotNull(mapper.apply(v.get()), "mapper cannot return null").get();
        } else {
            return Optional.empty();
        }
    }

}
