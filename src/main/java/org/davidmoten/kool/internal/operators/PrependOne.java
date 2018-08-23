package org.davidmoten.kool.internal.operators;

import java.util.Iterator;

public class PrependOne<T> implements Iterable<T> {

    private final T value;
    private final Iterable<T> source;

    public PrependOne(T value, Iterable<T> source) {
        this.value = value;
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            final Iterator<T> it = source.iterator();
            T value = PrependOne.this.value;

            @Override
            public boolean hasNext() {
                if (value != null) {
                    return true;
                } else {
                    return it.hasNext();
                }
            }

            @Override
            public T next() {
                T t = value;
                if (t != null) {
                    value = null;
                    return t;
                } else {
                    return it.next();
                }
            }

        };
    }

}
