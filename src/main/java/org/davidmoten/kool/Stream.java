package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.davidmoten.kool.internal.operators.Defer;
import org.davidmoten.kool.internal.operators.Filter;
import org.davidmoten.kool.internal.operators.First;
import org.davidmoten.kool.internal.operators.FlatMap;
import org.davidmoten.kool.internal.operators.Last;
import org.davidmoten.kool.internal.operators.Map;
import org.davidmoten.kool.internal.operators.OnValue;
import org.davidmoten.kool.internal.operators.PrependMany;
import org.davidmoten.kool.internal.operators.PrependOne;
import org.davidmoten.kool.internal.operators.Range;
import org.davidmoten.kool.internal.operators.Reduce1;
import org.davidmoten.kool.internal.operators.Take;
import org.davidmoten.kool.internal.util.Iterables;
import org.davidmoten.kool.internal.util.StreamImpl;
import org.davidmoten.kool.internal.util.StreamUtils;

public interface Stream<T> extends Iterable<T> {

    public static final int DEFAULT_BUFFER_SIZE = 16;

    static <T> Stream<T> create(Iterable<T> source) {
        return new StreamImpl<T>(source);
    }

    public default boolean isEmpty() {
        return !iterator().hasNext();
    }

    public default <R> Stream<R> map(Function<? super T, ? extends R> function) {
        return new Map<T, R>(function, this);
    }

    public default Maybe<T> reduce(BiFunction<? super T, ? super T, ? extends T> reducer) {
        return new Reduce1<T>(reducer, this).iterator().next();
    }

    public default <R> R reduce(R initialValue, BiFunction<? super R, ? super T, ? extends R> reducer) {
        // TODO Auto-generated method stub
        return null;
    }

    public default <R> R reduce(Supplier<R> initialValueFactory,
            BiFunction<? super R, ? super T, ? extends R> reducer) {
        // TODO Auto-generated method stub
        return null;
    }

    public default <R> R collect(Supplier<R> factory, BiConsumer<? super R, ? super T> collector) {
        // TODO Auto-generated method stub
        return null;
    }

    public default ArrayList<T> toJavaArrayList() {
        return toJavaArrayList(DEFAULT_BUFFER_SIZE);
    }

    public default ArrayList<T> toJavaArrayList(int sizeHint) {
        ArrayList<T> a = new ArrayList<>(sizeHint);
        Iterator<T> it = this.iterator();
        while (it.hasNext()) {
            a.add(it.next());
        }
        return a;
    }

    public default Stream<T> filter(Predicate<? super T> function) {
        if (function == Predicates.alwaysTrue()) {
            return this;
        } else {
            return new Filter<T>(function, this);
        }
    }

    public default long count() {
        Iterator<T> it = this.iterator();
        int i = 0;
        while (it.hasNext()) {
            it.next();
            i++;
        }
        return i;
    }

    public default Stream<T> prepend(T value) {
        return new PrependOne<T>(value, this);
    }

    public default Stream<T> prepend(T[] values) {
        return new PrependMany<T>(Iterables.fromArray(values), this);
    }

    public default Stream<T> prepend(List<? extends T> values) {
        return new PrependMany<T>(values, this);
    }

    public default <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> function) {
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

    public default Stream<T> onValue(Consumer<? super T> consumer) {
        return new OnValue<T>(consumer, this);
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

}
