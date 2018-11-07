package org.davidmoten.kool;

import java.util.function.Function;

import org.davidmoten.kool.internal.operators.single.Map;
import org.davidmoten.kool.internal.operators.single.SingleOf;
import org.davidmoten.kool.internal.operators.stream.SingleFlatMap;
import org.davidmoten.kool.internal.operators.stream.SingleToStream;

public interface Single<T> {

    public static <T> Single<T> of(T t) {
        return new SingleOf<T>(t);
    }

    public default <R> Single<R> map(Function<? super T, ? extends R> mapper) {
        return new Map<T, R>(mapper, this);
    }

    T get();

    public default <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return new SingleFlatMap<T, R>(this, mapper);
    }

    default SingleTester<T> test() {
        return new SingleTester<T>(this);
    }
    
    default Stream<T> toStream() {
        return new SingleToStream<T>(this);
    }

}
