package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Seq<T> extends Iterable<T> {

    boolean isEmpty();

    T reduce(BiFunction<T, T, T> reducer);

    <R> R reduce(R initialValue, BiFunction<R, T, R> reducer);

    <R> R reduce(Supplier<R> initialValueFactory, BiFunction<R, T, R> reducer);

    <R> R collect(Supplier<R> factory, BiConsumer<R, T> collector);

    <R> Seq<R> map(Function<T, R> function);

    <R> Seq<R> map(Function<T, R> function, int sizeHint);

    ArrayList<T> toJavaArrayList();

    ArrayList<T> toJavaArrayList(int sizeHint);

    Seq<T> filter(Predicate<T> function);

    Seq<T> filter(Predicate<T> function, int sizeHint);

    /**
     * Returns the number of elements in the list. O(N) algorithmic complexity.
     * 
     * @return the number of elements in the list
     */
    int count();

    Seq<T> prepend(T value);

    Seq<T> prepend(T[] values);

    Seq<T> prepend(List<T> values);

    <R> Seq<R> flatMap(Function<T, Seq<R>> function);

    <R> Seq<R> flatMap(Function<T, Seq<R>> function, int sizeHint);

    Optional<T> findFirst(Predicate<? super T> predicate);

    Iterator<T> iterator();
    
    Optional<T> first();
    
    Optional<T> last();
    
    Optional<T> get(int index);

}