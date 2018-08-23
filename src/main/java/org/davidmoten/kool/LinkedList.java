package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.github.davidmoten.guavamini.Preconditions;

/**
 * Non-lazy unidirectional linked list with functional style methods. Aims to be
 * memory and cpu sufficient within the constraints of the advertised data
 * structure.
 *
 * @param <T>
 */
public final class LinkedList<T> implements Iterable<T> {

    private static final int DEFAULT_BUFFER_SIZE = 16;
    private static final Object HEAD_NOT_PRESENT = new Object();
    private static final LinkedList<Object> NIL = new LinkedList<>(HEAD_NOT_PRESENT, null);

    private final T head;
    private final LinkedList<T> tail;

    LinkedList(T head, LinkedList<T> tail) {
        Preconditions.checkNotNull(head, "cannot prepend null to a LinkedList");
        this.head = head;
        this.tail = tail;
    }

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

    public <R> LinkedList<R> map(F1<T, R> function) {
        return map(function, 16);
    }

    public <R> LinkedList<R> map(F1<T, R> function, int sizeHint) {
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
    
    public ArrayList<T> toJavaArrayList() {
        return toJavaArrayList(DEFAULT_BUFFER_SIZE);
    }

    public ArrayList<T> toJavaArrayList(int sizeHint) {
        ArrayList<T> a = new ArrayList<>();
        for (T t : this) {
            a.add(t);
        }
        return a;
    }

    public LinkedList<T> filter(Predicate<T> function) {
        return filter(function, DEFAULT_BUFFER_SIZE);
    }

    public LinkedList<T> filter(Predicate<T> function, int sizeHint) {
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

    /**
     * Returns the number of elements in the list. O(N) algorithmic complexity.
     * 
     * @return the number of elements in the list
     */
    public int size() {
        int size = 0;
        LinkedList<T> x = this;
        while (x != NIL) {
            size++;
            x = x.tail;
        }
        return size;
    }

    public LinkedList<T> prepend(T value) {
        return new LinkedList<T>(value, this);
    }

    public LinkedList<T> prepend(T[] values) {
        LinkedList<T> x = this;
        for (int i = values.length - 1; i >= 0; i--) {
            x = x.prepend(values[i]);
        }
        return x;
    }

    public LinkedList<T> prepend(List<T> values) {
        LinkedList<T> x = this;
        for (int i = values.size() - 1; i >= 0; i--) {
            x = x.prepend(values.get(i));
        }
        return x;
    }

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
