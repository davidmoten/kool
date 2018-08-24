package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.davidmoten.kool.internal.operators.Filter;
import org.davidmoten.kool.internal.operators.First;
import org.davidmoten.kool.internal.operators.FlatMap;
import org.davidmoten.kool.internal.operators.Last;
import org.davidmoten.kool.internal.operators.Map;
import org.davidmoten.kool.internal.operators.PrependMany;
import org.davidmoten.kool.internal.operators.PrependOne;
import org.davidmoten.kool.internal.operators.Reduce1;
import org.davidmoten.kool.internal.util.Iterables;

public final class Stream<T> implements Seq<T> {

    private final Iterable<T> source;

    Stream(Iterable<T> source) {
        this.source = source;
    }

    static <T> Stream<T> create(Iterable<T> source) {
        return new Stream<T>(source);
    }

    @Override
    public boolean isEmpty() {
        return !source.iterator().hasNext();
    }

    @Override
    public <R> Stream<R> map(Function<? super T, ? extends R> function) {
        return create(new Map<T, R>(function, source));
    }

    @Override
    public Maybe<T> reduce(BiFunction<? super T, ? super T, ? extends T> reducer) {
        return create(new Reduce1<T>(reducer, source)).iterator().next();
    }

    @Override
    public <R> R reduce(R initialValue, BiFunction<? super R, ? super T, ? extends R> reducer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <R> R reduce(Supplier<R> initialValueFactory, BiFunction<? super R, ? super T, ? extends R> reducer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <R> R collect(Supplier<R> factory, BiConsumer<? super R, ? super T> collector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<T> toJavaArrayList(int sizeHint) {
        ArrayList<T> a = new ArrayList<>(sizeHint);
        Iterator<T> it = source.iterator();
        while (it.hasNext()) {
            a.add(it.next());
        }
        return a;
    }

    @Override
    public Stream<T> filter(Predicate<? super T> function) {
        return create(new Filter<T>(function, source));
    }

    @Override
    public long count() {
        Iterator<T> it = source.iterator();
        int i = 0;
        while (it.hasNext()) {
            it.next();
            i++;
        }
        return i;
    }

    @Override
    public Stream<T> prepend(T value) {
        return create(new PrependOne<T>(value, source));
    }

    @Override
    public Stream<T> prepend(T[] values) {
        return create(new PrependMany<T>(Iterables.fromArray(values), source));
    }

    @Override
    public Stream<T> prepend(List<? extends T> values) {
        return create(new PrependMany<T>(values, source));
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super T, ? extends Seq<? extends R>> function) {
        return create(new FlatMap<T, R>(function, source));
    }

    @Override
    public Maybe<T> findFirst(Predicate<? super T> predicate) {
        return filter(predicate).first();
    }

    @Override
    public Iterator<T> iterator() {
        return source.iterator();
    }

    @Override
    public Maybe<T> first() {
        return first(create(new First<T>(source)).iterator());
    }

    private static <T> Maybe<T> first(Iterator<T> it) {
        if (it.hasNext()) {
            return Maybe.of(it.next());
        } else {
            return Maybe.empty();
        }
    }

    @Override
    public Maybe<T> last() {
        return first(create(new Last<T>(source)).iterator());
    }

    @Override
    public Maybe<T> get(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    public static <T> Stream<T> of(T t) {
        return create(Collections.singleton(t));
    }

    public static <T> Stream<T> of(T t1, T t2) {
        return create(LinkedList.of(t1, t2));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3) {
        return create(LinkedList.of(t1, t2, t3));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4) {
        return create(LinkedList.of(t1, t2, t3, t4));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5) {
        return create(LinkedList.of(t1, t2, t3, t4, t5));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6) {
        return create(LinkedList.of(t1, t2, t3, t4, t5, t6));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7) {
        return create(LinkedList.of(t1, t2, t3, t4, t5, t6, t7));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8) {
        return create(LinkedList.of(t1, t2, t3, t4, t5, t6, t7, t8));
    }

    public static <T> Stream<T> from(Iterable<T> iterable) {
        return create(LinkedList.from(iterable));
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<T> empty() {
        return (Stream<T>) EmptyHolder.EMPTY;
    }

    private static final class EmptyHolder {
        public static final Stream<Object> EMPTY = Stream.create(Collections.emptyList());
    }

}
