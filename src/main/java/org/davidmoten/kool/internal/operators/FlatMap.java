package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.davidmoten.kool.Seq;

public final class FlatMap<T, R> implements Iterable<R> {

    private final Function<? super T, ? extends Seq<? extends R>> function;
    private final Iterable<T> source;

    public FlatMap(Function<? super T, ? extends Seq<? extends R>> function, Iterable<T> source) {
        this.function = function;
        this.source = source;
    }

    @Override
    public Iterator<R> iterator() {
        return new Iterator<R>() {

            final Iterator<T> a = source.iterator();
            Iterator<? extends R> b;
            R r;

            @Override
            public boolean hasNext() {
                loadNext();
                return r != null;
            }

            @Override
            public R next() {
                loadNext();
                if (r == null) {
                    throw new NoSuchElementException();
                } else {
                    R r2 = r;
                    r = null;
                    return r2;
                }
            }

            private void loadNext() {
                if (r != null) {
                    return;
                }
                while (true) {
                    if (b == null) {
                        if (a.hasNext()) {
                            b = function.apply(a.next()).iterator();
                            if (b.hasNext()) {
                                r = b.next();
                                return;
                            } else {
                                b = null;
                            }
                        } else {
                            return;
                        }
                    } else {
                        if (b.hasNext()) {
                            r = b.next();
                            return;
                        } else {
                            b = null;
                        }
                    }
                }
            }

        };
    }

}
