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
import org.davidmoten.kool.internal.operators.Map;

public class LazyLinkedList<T> implements Seq<T> {

    static final Builder BUILDER = new Builder();

    private final Iterable<T> source;

    LazyLinkedList(Iterable<T> source) {
        this.source = source;
    }

    static <T> LazyLinkedList<T> create(Iterable<T> source) {
        return new LazyLinkedList<T>(source);
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <R> Seq<R> map(Function<? super T, ? extends R> function) {
        return create(new Map<T, R>(function, source));
    }

    @Override
    public T reduce(BiFunction<? super T, ? super T, ? extends T> reducer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <R> R reduce(R initialValue, BiFunction<? super R, ? super T, ? extends R> reducer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <R> R reduce(Supplier<? extends R> initialValueFactory,
            BiFunction<? super R, ? super T, ? extends R> reducer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <R> R collect(Supplier<? extends R> factory, BiConsumer<? super R, ? super T> collector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<T> toJavaArrayList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<T> toJavaArrayList(int sizeHint) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Seq<T> filter(Predicate<? super T> function) {
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
    public Seq<T> prepend(T value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Seq<T> prepend(T[] values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Seq<T> prepend(List<? extends T> values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <R> Seq<R> flatMap(Function<? super T, ? extends Seq<? extends R>> function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <R> Seq<R> flatMap(Function<? super T, ? extends Seq<? extends R>> function, int sizeHint) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<T> findFirst(Predicate<? super T> predicate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<T> first() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<T> last() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<T> get(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    public static final class Builder {

        public <T> LazyLinkedList<T> of(T t) {
            return create(Collections.singleton(t));
        }

        public <T> LazyLinkedList<T> of(T t1, T t2) {
            return create(LinkedList.of(t1, t2));
        }
    }

}
