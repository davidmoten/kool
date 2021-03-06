package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Function;

import com.github.davidmoten.guavamini.Preconditions;

public final class Transform<T, R> implements Stream<R> {

    private final Function<? super Stream<T>, ? extends Stream<? extends R>> transformer;
    private final Stream<T> source;

    public Transform(Function<? super Stream<T>, ? extends Stream<? extends R>> transformer, Stream<T> source) {
        this.transformer = transformer;
        this.source = source;
    }

    @SuppressWarnings("unchecked")
    @Override
    public StreamIterator<R> iterator() {
        return Preconditions.checkNotNull((StreamIterator<R>) transformer.applyUnchecked(source).iteratorNullChecked());
    }

}
