package org.davidmoten.kool;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

import org.davidmoten.kool.internal.operators.single.Map;
import org.davidmoten.kool.internal.operators.single.SingleDoOnError;
import org.davidmoten.kool.internal.operators.single.SingleDoOnValue;
import org.davidmoten.kool.internal.operators.single.SingleError;
import org.davidmoten.kool.internal.operators.single.SingleFlatMap;
import org.davidmoten.kool.internal.operators.single.SingleFromCallable;
import org.davidmoten.kool.internal.operators.single.SingleIterator;
import org.davidmoten.kool.internal.operators.single.SingleOf;
import org.davidmoten.kool.internal.operators.single.SingleToStream;

public interface Single<T> extends StreamIterable<T> {
    
    T get();
    
    //////////////////
    // Factories
    //////////////////

    public static <T> Single<T> of(T t) {
        return new SingleOf<T>(t);
    }

    public static <T> Single<T> fromCallable(Callable<? extends T> callable) {
        return new SingleFromCallable<T>(callable);
    }
    
    public static <T> Single<T> error(Callable<? extends Throwable> callable) {
        return new SingleError<T>(callable);
    }
    
    public static <T> Single<T> error(Throwable error) {
        return error(() -> error);
    }

    //////////////////
    // Operators
    //////////////////
    
    public default <R> Single<R> map(Function<? super T, ? extends R> mapper) {
        return new Map<T, R>(mapper, this);
    }

    public default <R> Stream<R> flatMap(Function<? super T, ? extends StreamIterable<? extends R>> mapper) {
        return new SingleFlatMap<T, R>(this, mapper);
    }
    
    public default Single<T> doOnValue(Consumer<? super T> consumer) {
        return new SingleDoOnValue<T>(consumer, this);
    }

    public default Single<T> doOnError(Consumer<? super Throwable> consumer) {
        return new SingleDoOnError<T>(consumer, this);
    }

    default SingleTester<T> test() {
        return new SingleTester<T>(this);
    }

    default Stream<T> toStream() {
        return new SingleToStream<T>(this);
    }

    default StreamIterator<T> iterator() {
        return new SingleIterator<T>(this);
    }
    
    public default <R> R to(Function<? super Single<T>, R> mapper){
        return mapper.apply(this);
    }

}
