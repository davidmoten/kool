package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Last<T> implements Iterable<T> {

    private Iterable<T> source;

    public Last(Iterable<T> source) {
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Iterator<T> it = source.iterator();
            T t;

            @Override
            public boolean hasNext() {
                moveToLast();
                return it != null && t != null;
            }

            @Override
            public T next() {
                moveToLast();
                if (t == null) {
                    throw new NoSuchElementException();
                } else {
                    it = null;
                    return t;
                }
            }

            private void moveToLast() {
                if (it != null) {
                    while (it.hasNext()) {
                        t = it.next();
                    }
                }
            }
        };
    }

}
