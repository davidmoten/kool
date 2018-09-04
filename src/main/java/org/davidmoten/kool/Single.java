package org.davidmoten.kool;

import java.util.function.Function;

import org.davidmoten.kool.internal.operators.single.Map;
import org.davidmoten.kool.internal.operators.single.SingleOf;

public interface Single<T> extends StreamIterable<T> {

    public static <T> Single<T> of(T t) {
        return new SingleOf<T>(t);
    }

    public default <R> Single<R> map(Function<? super T, ? extends R> mapper) {
        return new Map<T, R>(mapper, this);
    }
    
    T value();

}
