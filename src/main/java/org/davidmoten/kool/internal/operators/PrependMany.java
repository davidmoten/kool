package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PrependMany<T> implements Iterable<T> {

    private final Iterable<? extends T> source1;
    private final Iterable<? extends T> source2;

    public PrependMany(Iterable<? extends T> source1, Iterable<? extends T> source2) {
        this.source1 = source1;
        this.source2 = source2;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Iterator<? extends T> a = source1.iterator();
            Iterator<? extends T> b = null;

            @Override
            public boolean hasNext() {
                return a.hasNext() || b().hasNext();
            }

            @Override
            public T next() {
                if (a.hasNext()) {
                    return a.next();
                } else if (b().hasNext()) {
                    return b().next();
                } else {
                    throw new NoSuchElementException();
                }
            }

            private Iterator<? extends T> b() {
                if (b == null) {
                    b = source2.iterator();
                }
                return b;
            }

        };
    }

}
