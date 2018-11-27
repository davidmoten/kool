package org.davidmoten.kool.internal.operators.single;

import java.util.Optional;
import java.util.function.Function;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.Single;

import com.github.davidmoten.guavamini.Preconditions;

public final class SingleFlatMapMaybe<T, R> implements Maybe<R> {

    private final Single<T> single;
    private final Function<? super T, ? extends Maybe<? extends R>> mapper;

    public SingleFlatMapMaybe(Single<T> single, Function<? super T, ? extends Maybe<? extends R>> mapper) {
        Preconditions.checkNotNull(mapper);
        this.single = single;
        this.mapper = mapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<R> get() {
        T v = single.get();
        Maybe<? extends R> maybe = Preconditions.checkNotNull(mapper.apply(v), "mapper cannot return null");
        return (Optional<R>) maybe.get();
    }

}
