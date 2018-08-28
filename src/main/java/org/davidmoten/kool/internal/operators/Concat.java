package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;

import com.github.davidmoten.guavamini.Preconditions;

public final class Concat<T> implements Stream<T> {

    private final Iterable<? extends T> source1;
    private final Iterable<? extends T> source2;

    public Concat(Iterable<? extends T> source1, Iterable<? extends T> source2) {
        this.source1 = source1;
        this.source2 = source2;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Iterator<? extends T> a = Preconditions.checkNotNull(source1.iterator());
            Iterator<? extends T> b = null;

            @Override
            public boolean hasNext() {
                if (a != null) {
                    if (a.hasNext()) {
                        return true;
                    } else {
                        // release a for gc
                        a = null;
                        return b().hasNext();
                    }
                } else {
                    return b().hasNext();
                }
            }

            @Override
            public T next() {
                if (a != null && a.hasNext()) {
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
