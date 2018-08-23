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
import org.davidmoten.kool.internal.operators.Prepend;
import org.davidmoten.kool.internal.operators.Reduce1;

public class LazySeq<T> implements Seq<T> {

    static final Builder BUILDER = new Builder();

    private final Iterable<T> source;

    LazySeq(Iterable<T> source) {
        this.source = source;
    }

    static <T> LazySeq<T> create(Iterable<T> source) {
        return new LazySeq<T>(source);
    }

    @Override
    public boolean isEmpty() {
        Iterator<T> it = source.iterator();
        return !it.hasNext();
    }

    @Override
    public <R> Seq<R> map(Function<? super T, ? extends R> function) {
        return create(new Map<T, R>(function, source));
    }

    @Override
    public Optional<T> reduce(BiFunction<? super T, ? super T, ? extends T> reducer) {
        return create(new Reduce1<T>(reducer, source)).iterator().next();
    }

    @Override
    public <R> R reduce(R initialValue, BiFunction<? super R, ? super T, ? extends R> reducer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <R> R reduce(Supplier< R> initialValueFactory,
            BiFunction<? super R, ? super T, ? extends R> reducer) {
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
        return create(new Prepend<T>(value, source));
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
        return source.iterator();
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

        public <T> LazySeq<T> of(T t) {
            return create(Collections.singleton(t));
        }

        public <T> LazySeq<T> of(T t1, T t2) {
            return create(LinkedList.of(t1, t2));
        }

        public <T> LazySeq<T> of(T t1, T t2, T t3) {
            return create(LinkedList.of(t1, t2, t3));
        }
        
        public <T> LazySeq<T> of(T t1, T t2, T t3, T t4) {
            return create(LinkedList.of(t1, t2, t3, t4));
        }

        public <T> LazySeq<T> of(T t1, T t2, T t3, T t4, T t5) {
            return create(LinkedList.of(t1, t2, t3, t4, t5));
        }
        
        public <T> LazySeq<T> of(T t1, T t2, T t3, T t4, T t5, T t6) {
            return create(LinkedList.of(t1, t2, t3, t4, t5, t6));
        }
        
        public <T> LazySeq<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7) {
            return create(LinkedList.of(t1, t2, t3, t4, t5, t6, t7));
        }
        
        public <T> LazySeq<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8) {
            return create(LinkedList.of(t1, t2, t3, t4, t5, t6, t7, t8));
        }
        
        public <T> LazySeq<T> from(Iterable<T> iterable) {
            return create(LinkedList.from(iterable));
        }
    }

}
