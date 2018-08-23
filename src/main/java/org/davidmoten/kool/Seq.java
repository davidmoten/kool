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

    T reduce(BiFunction<? super T, ? super T, ? extends T> reducer);

    <R> R reduce(R initialValue, BiFunction<? super R, ? super T, ? extends R> reducer);

    <R> R reduce(Supplier<? extends R> initialValueFactory, BiFunction<? super R, ? super T, ? extends R> reducer);

    <R> R collect(Supplier<? extends R> factory, BiConsumer<? super R, ? super T> collector);

    <R> Seq<R> map(Function<? super T, ? extends R> function);

    ArrayList<T> toJavaArrayList();

    ArrayList<T> toJavaArrayList(int sizeHint);

    Seq<T> filter(Predicate<? super T> function);

    /**
     * Returns the number of elements in the list. O(N) algorithmic complexity.
     * 
     * @return the number of elements in the list
     */
    long count();

    Seq<T> prepend(T value);

    Seq<T> prepend(T[] values);

    Seq<T> prepend(List<? extends T> values);

    <R> Seq<R> flatMap(Function<? super T, ? extends Seq<? extends R>> function);

    <R> Seq<R> flatMap(Function<? super T, ? extends Seq<? extends R>> function, int sizeHint);

    Optional<T> findFirst(Predicate<? super T> predicate);

    Iterator<T> iterator();
    
    Optional<T> first();
    
    Optional<T> last();
    
    Optional<T> get(int index);

}