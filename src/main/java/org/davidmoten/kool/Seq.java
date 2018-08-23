package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Seq<T> {

    boolean isEmpty();

    T reduce(BiFunction<T, T, T> reducer);

    <R> R reduce(R initialValue, BiFunction<R, T, R> reducer);

    <R> R reduce(Supplier<R> initialValueFactory, BiFunction<R, T, R> reducer);

    <R> R collect(Supplier<R> factory, BiConsumer<R, T> collector);

    <R> LinkedList<R> map(Function<T, R> function);

    <R> LinkedList<R> map(Function<T, R> function, int sizeHint);

    ArrayList<T> toJavaArrayList();

    ArrayList<T> toJavaArrayList(int sizeHint);

    LinkedList<T> filter(Predicate<T> function);

    LinkedList<T> filter(Predicate<T> function, int sizeHint);

    /**
     * Returns the number of elements in the list. O(N) algorithmic complexity.
     * 
     * @return the number of elements in the list
     */
    int count();

    LinkedList<T> prepend(T value);

    LinkedList<T> prepend(T[] values);

    LinkedList<T> prepend(List<T> values);

    <R> LinkedList<R> flatMap(Function<T, LinkedList<R>> function);

    <R> LinkedList<R> flatMap(Function<T, LinkedList<R>> function, int sizeHint);

    Optional<T> findFirst(Predicate<? super T> predicate);

    Iterator<T> iterator();
    
    Optional<T> first();
    
    Optional<T> last();
    
    Optional<T> get(int index);

}