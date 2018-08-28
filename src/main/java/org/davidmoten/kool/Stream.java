package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.davidmoten.kool.internal.operators.Concat;
import org.davidmoten.kool.internal.operators.Defer;
import org.davidmoten.kool.internal.operators.DoOnComplete;
import org.davidmoten.kool.internal.operators.DoOnError;
import org.davidmoten.kool.internal.operators.DoOnNext;
import org.davidmoten.kool.internal.operators.Filter;
import org.davidmoten.kool.internal.operators.First;
import org.davidmoten.kool.internal.operators.FlatMap;
import org.davidmoten.kool.internal.operators.Last;
import org.davidmoten.kool.internal.operators.Map;
import org.davidmoten.kool.internal.operators.PrependOne;
import org.davidmoten.kool.internal.operators.Range;
import org.davidmoten.kool.internal.operators.Reduce1;
import org.davidmoten.kool.internal.operators.SwitchOnError;
import org.davidmoten.kool.internal.operators.Take;
import org.davidmoten.kool.internal.operators.Transform;
import org.davidmoten.kool.internal.operators.Zip;
import org.davidmoten.kool.internal.util.Iterables;
import org.davidmoten.kool.internal.util.StreamImpl;
import org.davidmoten.kool.internal.util.StreamUtils;

public interface Stream<T> extends StreamIterable<T> {

    public static final int DEFAULT_BUFFER_SIZE = 16;

    static <T> Stream<T> create(StreamIterable<T> source) {
        return new StreamImpl<T>(source);
    }

    static <T> Stream<T> create(Iterable<T> source) {
        return new StreamImpl<T>(source);
    }

    public static <T> Stream<T> of(T t) {
        return create(Collections.singleton(t));
    }

    public static <T> Stream<T> of(T t1, T t2) {
        return create(Iterables.ofNoCopy(t1, t2));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3) {
        return create(Iterables.ofNoCopy(t1, t2, t3));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5, t6));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5, t6, t7));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5, t6, t7, t8));
    }

    public static <T> Stream<T> error(Throwable e) {
        return Stream.from(new StreamIterable<T>() {
            @Override
            public StreamIterator<T> iterator() {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else if (e instanceof Error) {
                    throw (Error) e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static <T> Stream<T> from(Iterable<T> iterable) {
        return create(iterable);
    }

    public static Stream<Long> range(long start, long length) {
        return create(new Range(start, length));
    }

    public static Stream<Long> ordinals() {
        return range(1, Long.MAX_VALUE);
    }

    public static <T> Stream<T> defer(Supplier<? extends Stream<? extends T>> supplier) {
        return new Defer<T>(supplier);
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<T> empty() {
        return (Stream<T>) StreamUtils.EmptyHolder.EMPTY;
    }

    public default boolean isEmpty() {
        StreamIterator<T> it = iterator();
        boolean r = !it.hasNext();
        it.cancel();
        return r;
    }

    public default <R> Stream<R> map(Function<? super T, ? extends R> function) {
        return new Map<T, R>(function, this);
    }

    public default Maybe<T> reduce(BiFunction<? super T, ? super T, ? extends T> reducer) {
        return new Reduce1<T>(reducer, this).iterator().next();
    }

    public default <R> R reduce(R initialValue,
            BiFunction<? super R, ? super T, ? extends R> reducer) {
        // TODO Auto-generated method stub
        return null;
    }

    public default <R> R reduce(Supplier<R> initialValueFactory,
            BiFunction<? super R, ? super T, ? extends R> reducer) {
        // TODO Auto-generated method stub
        return null;
    }

    public default <R> R collect(Supplier<? extends R> factory,
            BiConsumer<? super R, ? super T> collector) {
        StreamIterator<T> it = iterator();
        R c = factory.get();
        while (it.hasNext()) {
            collector.accept(c, it.next());
        }
        it.cancel();
        return c;
    }

    public default List<T> toList() {
        return toList(DEFAULT_BUFFER_SIZE);
    }

    public default Set<T> toSet() {
        return toSet(DEFAULT_BUFFER_SIZE);
    }

    public default Set<T> toSet(int sizeHint) {
        return Iterables.addAll(new HashSet<T>(sizeHint), this);
    }

    public default List<T> toList(int sizeHint) {
        return Iterables.addAll(new ArrayList<T>(sizeHint), this);
    }

    public default Stream<T> filter(Predicate<? super T> function) {
        if (function == Predicates.alwaysTrue()) {
            return this;
        } else {
            return new Filter<T>(function, this);
        }
    }

    public default void forEach() {
        count();
    }

    public default void ignoreElements() {
        count();
    }

    public default long count() {
        StreamIterator<T> it = this.iterator();
        int i = 0;
        while (it.hasNext()) {
            it.next();
            i++;
        }
        it.cancel();
        return i;
    }

    public default Stream<T> prepend(T value) {
        return new PrependOne<T>(value, this);
    }

    public default Stream<T> prepend(T[] values) {
        return new Concat<T>(create(Iterables.fromArray(values)), this);
    }

    public default Stream<T> prepend(StreamIterable<? extends T> values) {
        return new Concat<T>(values, this);
    }

    public default Stream<T> concatWith(StreamIterable<? extends T> values) {
        return new Concat<T>(this, values);
    }

    public default <R> Stream<R> flatMap(
            Function<? super T, ? extends Stream<? extends R>> function) {
        return new FlatMap<T, R>(function, this);
    }

    public default Maybe<T> findFirst(Predicate<? super T> predicate) {
        if (predicate == Predicates.alwaysFalse()) {
            return Maybe.empty();
        } else {
            return filter(predicate).first();
        }
    }

    public default Maybe<T> first() {
        return Iterables.first(new First<T>(this).iterator());
    }

    public default Stream<T> doOnNext(Consumer<? super T> consumer) {
        return new DoOnNext<T>(consumer, this);
    }

    public default Stream<T> doOnError(Consumer<? super Throwable> consumer) {
        return new DoOnError<T>(consumer, this);
    }

    public default Stream<T> doOnComplete(Runnable action) {
        return new DoOnComplete<T>(action, this);
    }

    public default Maybe<T> last() {
        return Iterables.first(new Last<T>(this).iterator());
    }

    public default Maybe<T> get(int index) {
        return take(index + 1).last();
    }

    public default Stream<T> take(long n) {
        return new Take<T>(n, this);
    }

    public default <R> Stream<R> transform(
            Function<? super Stream<T>, ? extends Stream<? extends R>> transformer) {
        return new Transform<T, R>(transformer, this);
    }

    public default Stream<T> switchOnError(
            Function<? super Throwable, ? extends Stream<? extends T>> function) {
        return new SwitchOnError<T>(function, this);
    }

    public default <R, S> Stream<S> zipWith(Stream<? extends R> stream,
            BiFunction<T, R, S> combiner) {
        return new Zip<R, S, T>(this, stream, combiner);
    }

    // TODO
    // takeUntil, takeWhile, buffer, bufferWhile, bufferUntil, toMap, toStreamJava ,
    // mapWithIndex, skip, skipUntil, skipWhile, sorted, repeat, retry, cache,
    // groupBy, doOnEmpty, switchIfEmpty, interleave

}
