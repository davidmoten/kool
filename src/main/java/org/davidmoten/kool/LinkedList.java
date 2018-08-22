package org.davidmoten.kool;

import java.util.Iterator;
import java.util.NoSuchElementException;

//Non-lazy linked list with functional style methods
public class LinkedList<T> implements Iterable<T> {

    private static final LinkedList<Object> NIL = new LinkedList<>(null, null);

    private final T head;
    private final LinkedList<T> tail;

    LinkedList(T head, LinkedList<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    public T head() {
        return head;
    }

    public LinkedList<T> tail() {
        return tail;
    }

    @SuppressWarnings("unchecked")
    public static <R> LinkedList<R> nil() {
        return (LinkedList<R>) NIL;
    }

    public <R> LinkedList<R> map(F1<T, R> function) {
        if (this == NIL) {
            return nil();
        } else {
            return new LinkedList<R>(function.apply(head), tail.map(function));
        }
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

}
