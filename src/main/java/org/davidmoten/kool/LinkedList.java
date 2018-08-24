package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.davidmoten.guavamini.Preconditions;

import static org.davidmoten.kool.internal.operators.Constants.DEFAULT_BUFFER_SIZE;

/**
 * Non-lazy unidirectional linked list with functional style methods. Aims to be
 * memory and cpu efficient within the normal constraints of a linked list.
 *
 * @param <T> type of item in linst
 */
public final class LinkedList<T> implements Seq<T> {

    private static final Object HEAD_NOT_PRESENT = new Object();
    private static final LinkedList<Object> NIL = new LinkedList<>(HEAD_NOT_PRESENT, null);

    private final T head;
    private final LinkedList<T> tail;

    LinkedList(T head, LinkedList<T> tail) {
        Preconditions.checkNotNull(head, "cannot prepend null to a LinkedList");
        this.head = head;
        this.tail = tail;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return this == NIL;
    }

    public T head() {
        if (isEmpty()) {
            throw new NoSuchElementException("list is empty");
        } else {
            return head;
        }
    }

    public LinkedList<T> tail() {
        return tail;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#reduce(java.util.function.BiFunction)
     */
    @Override
    public Maybe<T> reduce(BiFunction<? super T, ? super T, ? extends T> reducer) {
        if (isEmpty()) {
            // TODO better exception name?
            return Maybe.empty();
        } else {
            return Maybe.of(tail.<T>reduce(head, reducer));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#reduce(R, java.util.function.BiFunction)
     */
    @Override
    public <R> R reduce(R initialValue, BiFunction<? super R, ? super T, ? extends R> reducer) {
        R r = initialValue;
        LinkedList<T> x = this;
        while (!x.isEmpty()) {
            T v = x.head;
            r = reducer.apply(r, v);
            x = x.tail;
        }
        return r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#reduce(java.util.function.Supplier,
     * java.util.function.BiFunction)
     */
    @Override
    public <R> R reduce(Supplier<R> initialValueFactory, BiFunction<? super R, ? super T, ? extends R> reducer) {
        R r = initialValueFactory.get();
        return reduce(r, reducer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#collect(java.util.function.Supplier,
     * java.util.function.BiConsumer)
     */
    @Override
    public <R> R collect(Supplier<R> factory, BiConsumer<? super R, ? super T> collector) {
        R r = factory.get();
        LinkedList<T> x = this;
        while (!x.isEmpty()) {
            T v = x.head;
            collector.accept(r, v);
            x = x.tail;
        }
        return r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#map(java.util.function.Function)
     */
    @Override
    public <R> LinkedList<R> map(Function<? super T, ? extends R> function) {
        return map(function, 16);
    }

    public <R> LinkedList<R> map(Function<? super T, ? extends R> function, int sizeHint) {
        if (this == NIL) {
            return nil();
        } else {
            ArrayList<R> a = new ArrayList<R>(sizeHint);
            {
                LinkedList<T> x = this;
                while (x != NIL) {
                    a.add(function.apply(x.head));
                    x = x.tail;
                }
            }
            LinkedList<R> x = nil();
            for (int i = a.size() - 1; i >= 0; i--) {
                x = x.prepend(a.get(i));
            }
            return x;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#toJavaArrayList()
     */
    @Override
    public ArrayList<T> toJavaArrayList() {
        return toJavaArrayList(DEFAULT_BUFFER_SIZE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#toJavaArrayList(int)
     */
    @Override
    public ArrayList<T> toJavaArrayList(int sizeHint) {
        ArrayList<T> a = new ArrayList<>();
        LinkedList<T> x = this;
        while (!x.isEmpty()) {
            a.add(x.head);
            x = x.tail;
        }
        return a;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#filter(java.util.function.Predicate)
     */
    @Override
    public LinkedList<T> filter(Predicate<? super T> function) {
        return filter(function, DEFAULT_BUFFER_SIZE);
    }

    public LinkedList<T> filter(Predicate<? super T> function, int sizeHint) {
        if (this == NIL) {
            return nil();
        } else {
            ArrayList<T> a = new ArrayList<T>(sizeHint);
            {
                LinkedList<T> x = this;
                while (x != NIL) {
                    boolean include = function.test(x.head);
                    if (include) {
                        a.add(x.head);
                    }
                    x = x.tail;
                }
            }
            LinkedList<T> x = nil();
            for (int i = a.size() - 1; i >= 0; i--) {
                x = x.prepend(a.get(i));
            }
            return x;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#count()
     */
    @Override
    public long count() {
        long size = 0;
        LinkedList<T> x = this;
        while (x != NIL) {
            size++;
            x = x.tail;
        }
        return size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#prepend(T)
     */
    @Override
    public LinkedList<T> prepend(T value) {
        return new LinkedList<T>(value, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#prepend(T[])
     */
    @Override
    public LinkedList<T> prepend(T[] values) {
        LinkedList<T> x = this;
        for (int i = values.length - 1; i >= 0; i--) {
            x = x.prepend(values[i]);
        }
        return x;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#prepend(java.util.List)
     */
    @Override
    public LinkedList<T> prepend(List<? extends T> values) {
        LinkedList<T> x = this;
        for (int i = values.size() - 1; i >= 0; i--) {
            x = x.prepend(values.get(i));
        }
        return x;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#flatMap(java.util.function.Function)
     */
    @Override
    public <R> LinkedList<R> flatMap(Function<? super T, ? extends Seq<? extends R>> function) {
        return flatMap(function, DEFAULT_BUFFER_SIZE);
    }

    public <R> LinkedList<R> flatMap(Function<? super T, ? extends Seq<? extends R>> function, int sizeHint) {
        ArrayList<R> a = new ArrayList<R>(sizeHint);
        LinkedList<T> x = this;
        while (!x.isEmpty()) {
            Seq<? extends R> y = function.apply(x.head);
            for (R r : y) {
                a.add(r);
            }
            x = x.tail;
        }
        return LinkedList.from(a);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#findFirst(java.util.function.Predicate)
     */
    @Override
    public Maybe<T> findFirst(Predicate<? super T> predicate) {
        LinkedList<T> x = this;
        while (!x.isEmpty()) {
            if (predicate.test(x.head)) {
                return Maybe.of(x.head);
            }
            x = x.tail;
        }
        return Maybe.empty();
    }

    @Override
    public Maybe<T> first() {
        if (isEmpty()) {
            return Maybe.empty();
        } else {
            return Maybe.of(head);
        }
    }

    @Override
    public Maybe<T> last() {
        LinkedList<T> x = this;
        while (!x.isEmpty()) {
            if (x.tail.isEmpty()) {
                return Maybe.of(x.head);
            }
            x = x.tail;
        }
        return Maybe.empty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.davidmoten.kool.Seq#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            LinkedList<T> list = LinkedList.this;

            @Override
            public boolean hasNext() {
                return list != NIL;
            }

            @Override
            public T next() {
                if (list == NIL) {
                    throw new NoSuchElementException();
                } else {
                    T t = list.head;
                    list = list.tail;
                    return t;
                }
            }
        };
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((head == null) ? 0 : head.hashCode());
        result = prime * result + ((tail == null) ? 0 : tail.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LinkedList<?> other = (LinkedList<?>) obj;
        if (head == null) {
            if (other.head != null)
                return false;
        } else if (!head.equals(other.head))
            return false;
        if (tail == null) {
            if (other.tail != null)
                return false;
        } else if (!tail.equals(other.tail))
            return false;
        return true;
    }

    @Override
    public Maybe<T> get(int index) {
        LinkedList<T> x = this;
        while (index > 0) {
            if (x.isEmpty()) {
                return Maybe.empty();
            }
            index--;
            x = x.tail;
        }
        if (x.isEmpty()) {
            return Maybe.empty();
        } else {
            return Maybe.of(x.head);
        }
    }

    // factory methods

    @SuppressWarnings("unchecked")
    public static <R> LinkedList<R> nil() {
        return (LinkedList<R>) NIL;
    }

    public static <T> LinkedList<T> of(T t1) {
        return new LinkedList<T>(t1, nil());
    }

    public static <T> LinkedList<T> of(T t1, T t2) {
        return of(t2).prepend(t1);
    }

    public static <T> LinkedList<T> of(T t1, T t2, T t3) {
        return of(t3).prepend(t2).prepend(t1);
    }

    public static <T> LinkedList<T> of(T t1, T t2, T t3, T t4) {
        return of(t4).prepend(t3).prepend(t2).prepend(t1);
    }

    public static <T> LinkedList<T> of(T t1, T t2, T t3, T t4, T t5) {
        return of(t5).prepend(t4).prepend(t3).prepend(t2).prepend(t1);
    }

    public static <T> LinkedList<T> of(T t1, T t2, T t3, T t4, T t5, T t6) {
        return of(t6).prepend(t5).prepend(t4).prepend(t3).prepend(t2).prepend(t1);
    }

    public static <T> LinkedList<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7) {
        return of(t7).prepend(t6).prepend(t5).prepend(t4).prepend(t3).prepend(t2).prepend(t1);
    }

    public static <T> LinkedList<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8) {
        return of(t8).prepend(t7).prepend(t6).prepend(t5).prepend(t4).prepend(t3).prepend(t2).prepend(t1);
    }

    public static <T> LinkedList<T> from(T[] values) {
        return LinkedList.<T>nil().prepend(values);
    }

    public static <T> LinkedList<T> from(Iterable<T> values) {
        List<T> a = new ArrayList<>();
        for (T t : values) {
            a.add(t);
        }
        return LinkedList.<T>nil().prepend(a);
    }

}
