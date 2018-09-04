package org.davidmoten.kool.internal.operators.single;

import java.util.function.Function;

import org.davidmoten.kool.Single;

import com.github.davidmoten.guavamini.Preconditions;

public final class Map<T, R> implements Single<R> {

    private final Function<? super T, ? extends R> mapper;
    private final Single<T> source;

    public Map(Function<? super T, ? extends R> mapper, Single<T> source) {
        Preconditions.checkNotNull(mapper);
        Preconditions.checkNotNull(source);
        this.mapper = mapper;
        this.source = source;
    }

    @Override
    public R get() {
        return Preconditions.checkNotNull(mapper.apply(source.get()));
    }

}
