package org.davidmoten.kool;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.internal.operators.single.Map;
import org.davidmoten.kool.internal.operators.single.SingleDoOnError;
import org.davidmoten.kool.internal.operators.single.SingleDoOnValue;
import org.davidmoten.kool.internal.operators.single.SingleError;
import org.davidmoten.kool.internal.operators.single.SingleFlatMap;
import org.davidmoten.kool.internal.operators.single.SingleFlatMapMaybe;
import org.davidmoten.kool.internal.operators.single.SingleFromCallable;
import org.davidmoten.kool.internal.operators.single.SingleIterator;
import org.davidmoten.kool.internal.operators.single.SingleOf;
import org.davidmoten.kool.internal.operators.single.SingleSwitchOnError;
import org.davidmoten.kool.internal.operators.single.SingleToStream;

import com.github.davidmoten.guavamini.Preconditions;

public interface Single<T> extends StreamIterable<T> {

    T get();

    //////////////////
    // Factories
    //////////////////

    static <T> Single<T> of(T t) {
        return new SingleOf<T>(t);
    }

    static <T> Single<T> fromCallable(Callable<? extends T> callable) {
        return new SingleFromCallable<T>(callable);
    }

    static <T> Single<T> error(Callable<? extends Throwable> callable) {
        return new SingleError<T>(callable);
    }

    static <T> Single<T> error(Throwable error) {
        return error(() -> error);
    }

    static Single<Integer> timer(long duration, TimeUnit unit) {
        return timer(1, duration, unit);
    }

    static <T> Single<T> timer(T t, long duration, TimeUnit unit) {
        Preconditions.checkNotNull(t);
        Preconditions.checkNotNull(unit);
        Preconditions.checkArgument(duration >= 0);
        return fromCallable(() -> {
            unit.sleep(duration);
            return t;
        });
    }

    //////////////////
    // Operators
    //////////////////

    default <R> Single<R> map(Function<? super T, ? extends R> mapper) {
        return new Map<T, R>(mapper, this);
    }

    default <R> Stream<R> flatMap(Function<? super T, ? extends StreamIterable<? extends R>> mapper) {
        return new SingleFlatMap<T, R>(this, mapper);
    }

    default <R> Maybe<R> flatMapMaybe(Function<? super T, ? extends Maybe<? extends R>> mapper) {
        return new SingleFlatMapMaybe<T, R>(this, mapper);
    }

    default Single<T> doOnValue(Consumer<? super T> consumer) {
        return new SingleDoOnValue<T>(consumer, this);
    }

    default Single<T> doOnError(Consumer<? super Throwable> consumer) {
        return new SingleDoOnError<T>(consumer, this);
    }
    
    default <R> Single<T> switchOnError(Function<? super Throwable, ? extends Single<? extends T>> function) {
        return new SingleSwitchOnError<T>(this, function);
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

    default <R> R to(Function<? super Single<T>, R> mapper) {
        return mapper.applyUnchecked(this);
    }

    default void forEach() {
        get();
    }
    
    default void go() {
        get();
    }
    
    default void start() {
        get();
    }
    
    default Stream<T> repeat() {
        return toStream().repeat();
    }

    default Single<T> retryWhen(Function<? super Throwable, ? extends Single<?>> function) {
        return toStream().retryWhen(function).single();
    }

    default RetryWhenBuilderSingle<T> retryWhen() {
        return new RetryWhenBuilderSingle<T>(this);
    }
    
    default Single<T> println() {
        return doOnValue(System.out::println);
    }
}
