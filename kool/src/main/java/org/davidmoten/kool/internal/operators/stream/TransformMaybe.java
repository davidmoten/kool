package org.davidmoten.kool.internal.operators.stream;

import java.util.Optional;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.function.Function;

public final class TransformMaybe<T, R> implements Maybe<R> {

    private final Stream<T> stream;
    private final Function<? super Stream<T>, ? extends Maybe<? extends R>> transformer;

    public TransformMaybe(Function<? super Stream<T>, ? extends Maybe<? extends R>> transformer, Stream<T> stream) {
        this.stream = stream;
        this.transformer = transformer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<R> get() {
        return (Optional<R>) transformer.applyUnchecked(stream).get();
    }

}
