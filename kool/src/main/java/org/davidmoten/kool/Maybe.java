package org.davidmoten.kool;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.davidmoten.kool.function.Consumer;
import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.function.Predicate;
import org.davidmoten.kool.internal.operators.maybe.MaybeDefer;
import org.davidmoten.kool.internal.operators.maybe.MaybeDoOnEmpty;
import org.davidmoten.kool.internal.operators.maybe.MaybeDoOnError;
import org.davidmoten.kool.internal.operators.maybe.MaybeDoOnValue;
import org.davidmoten.kool.internal.operators.maybe.MaybeError;
import org.davidmoten.kool.internal.operators.maybe.MaybeFilter;
import org.davidmoten.kool.internal.operators.maybe.MaybeFlatMap;
import org.davidmoten.kool.internal.operators.maybe.MaybeFlatMapMaybe;
import org.davidmoten.kool.internal.operators.maybe.MaybeFromCallable;
import org.davidmoten.kool.internal.operators.maybe.MaybeIsPresent;
import org.davidmoten.kool.internal.operators.maybe.MaybeIterator;
import org.davidmoten.kool.internal.operators.maybe.MaybeMap;
import org.davidmoten.kool.internal.operators.maybe.MaybeOrElse;
import org.davidmoten.kool.internal.operators.maybe.MaybeSwitchOnError;
import org.davidmoten.kool.internal.operators.maybe.MaybeToStream;
import org.davidmoten.kool.internal.util.MaybeImpl;

import com.github.davidmoten.guavamini.Preconditions;

public interface Maybe<T> extends StreamIterable<T> {

    Optional<T> get();

    //////////////////
    // Factories
    //////////////////

    static <T> Maybe<T> of(T value) {
        Preconditions.checkNotNull(value);
        return new MaybeImpl<T>(Optional.of(value));
    }

    static <T> Maybe<T> fromOptional(Optional<? extends T> optional) {
        if (optional.isPresent()) {
            return Maybe.of(optional.get());
        } else {
            return Maybe.empty();
        }
    }

    static <T> Maybe<T> fromCallableNullable(Callable<? extends T> callable) {
        return new MaybeFromCallable<T>(callable, true);
    }

    static <T> Maybe<T> fromCallable(Callable<? extends T> callable) {
        return new MaybeFromCallable<T>(callable, false);
    }

    static <T> Maybe<T> ofNullable(T value) {
        if (value == null) {
            return empty();
        } else {
            return new MaybeImpl<T>(Optional.of(value));
        }
    }

    @SuppressWarnings("unchecked")
    static <T> Maybe<T> empty() {
        return (Maybe<T>) MaybeImpl.EmptyHolder.INSTANCE;
    }

    static <T> Maybe<T> defer(Callable<? extends Maybe<? extends T>> factory) {
        return new MaybeDefer<T>(factory);
    }

    static <T> Maybe<T> error(Callable<? extends Throwable> callable) {
        return new MaybeError<T>(callable);
    }

    static <T> Maybe<T> error(Throwable error) {
        return error(() -> error);
    }

    //////////////////
    // Operators
    //////////////////

    default <R> Maybe<R> map(Function<? super T, ? extends R> mapper) {
        return new MaybeMap<T, R>(this, mapper);
    }

    default Stream<T> toStream() {
        return new MaybeToStream<T>(this);
    }

    default <R> Stream<R> flatMap(Function<? super T, ? extends StreamIterable<? extends R>> mapper) {
        return new MaybeFlatMap<T, R>(this, mapper);
    }
    
    default Maybe<T> doOnValue(Consumer<? super T> consumer) {
        return new MaybeDoOnValue<T>(consumer, this);
    }

    default Maybe<T> doOnError(Consumer<? super Throwable> consumer) {
        return new MaybeDoOnError<T>(consumer, this);
    }
    
    default Maybe<T> filter(Predicate<? super T> predicate) {
        return new MaybeFilter<T>(predicate, this);
    }

    default Maybe<T> doOnEmpty(Runnable action) {
        return new MaybeDoOnEmpty<T>(this, action);
    }

    default Single<T> orElse(T value) {
        return new MaybeOrElse<T>(this, value);
    }

    default MaybeTester<T> test() {
        return new MaybeTester<T>(this);
    }

    default Single<Boolean> isPresent() {
        return new MaybeIsPresent(this);
    }

    default StreamIterator<T> iterator() {
        return new MaybeIterator<T>(this);
    }

    default <R> R to(Function<? super Maybe<T>, R> mapper) {
        return mapper.applyUnchecked(this);
    }

    default void forEach() {
        get();
    }
    
    default void start() {
        get();
    }
    
    default void go() {
        get();
    }

    default Maybe<T> switchOnError(Function<? super Throwable, ? extends Maybe<? extends T>> function) {
        return new MaybeSwitchOnError<T>(this, function);
    }

    default <R> Maybe<R> flatMapMaybe(Function<? super T, ? extends Maybe<? extends R>> mapper) {
        return new MaybeFlatMapMaybe<T, R>(this, mapper);
    }
    
    default Maybe<T> retryWhen(Function<? super Throwable, ? extends Single<?>> function) {
        return toStream().retryWhen(function).maybe();
    }

    default RetryWhenBuilderMaybe<T> retryWhen() {
        return new RetryWhenBuilderMaybe<T>(this);
    }

    default Maybe<T> println() {
        return doOnValue(System.out::println);
    }
    
    default Single<Long> count() {
        return toStream().count();
    }

}
