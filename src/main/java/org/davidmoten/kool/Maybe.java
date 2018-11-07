package org.davidmoten.kool;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.davidmoten.kool.internal.operators.maybe.MaybeDoOnEmpty;
import org.davidmoten.kool.internal.operators.maybe.MaybeDoOnError;
import org.davidmoten.kool.internal.operators.maybe.MaybeDoOnValue;
import org.davidmoten.kool.internal.operators.maybe.MaybeFlatMap;
import org.davidmoten.kool.internal.operators.maybe.MaybeIsPresent;
import org.davidmoten.kool.internal.operators.maybe.MaybeMap;
import org.davidmoten.kool.internal.operators.maybe.MaybeToStream;
import org.davidmoten.kool.internal.util.MaybeImpl;

import com.github.davidmoten.guavamini.Preconditions;

public interface Maybe<T> {

    Optional<T> get();

    public static <T> Maybe<T> of(T value) {
        Preconditions.checkNotNull(value);
        return new MaybeImpl<T>(Optional.of(value));
    }

    @SuppressWarnings("unchecked")
    public static <T> Maybe<T> empty() {
        return (Maybe<T>) MaybeImpl.EmptyHolder.INSTANCE;
    }

    public default <R> Maybe<R> map(Function<? super T, ? extends R> mapper) {
        return new MaybeMap<T, R>(this, mapper);
    }

    public default Stream<T> toStream() {
        return new MaybeToStream<T>(this);
    }
    
    public default <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return new MaybeFlatMap<T, R>(this, mapper);
    }
    
    public default Maybe<T> doOnValue(Consumer<? super T> consumer) {
        return new MaybeDoOnValue<T>(consumer, this);
    }

    public default Maybe<T> doOnError(Consumer<? super Throwable> consumer) {
        return new MaybeDoOnError<T>(consumer, this);
    }

    public default Maybe<T> doOnEmpty(Runnable action) {
        return new MaybeDoOnEmpty<T>(this, action);
    }
    
    default MaybeTester<T> test() {
        return new MaybeTester<T>(this);
    }
    
    default Single<Boolean> isPresent() {
        return new MaybeIsPresent(this);
    }
    
}
