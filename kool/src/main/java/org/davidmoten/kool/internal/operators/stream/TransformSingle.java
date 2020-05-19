package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.function.Function;

public final class TransformSingle<T, R> implements Single<R> {

    private final Stream<T> stream;
    private final Function<? super Stream<T>, ? extends Single<? extends R>> transformer;

    public TransformSingle(Function<? super Stream<T>, ? extends Single<? extends R>> transformer, Stream<T> stream) {
        this.stream = stream;
        this.transformer = transformer;
    }

    @Override
    public R get() {
        return transformer.applyUnchecked(stream).get();
    }

}
