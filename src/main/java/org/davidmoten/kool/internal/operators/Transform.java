package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.function.Function;

import org.davidmoten.kool.Stream;

public final class Transform<T, R> implements Stream<R>{

    private final Function<? super Stream<T>, ? extends Stream<? extends R>> transformer;
    private final Stream<T> source;

    public Transform(Function<? super Stream<T>, ? extends Stream<? extends R>> transformer, Stream<T> source) {
        this.transformer = transformer;
        this.source = source;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<R> iterator() {
        return (Iterator<R>) transformer.apply(source).iterator();
    }

}
