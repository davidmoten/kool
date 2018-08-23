package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
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

            Iterator<T> it = source.iterator();
            Queue<R> queue = new LinkedList<R>();

            @Override
            public boolean hasNext() {
                checkNext();
                return !queue.isEmpty();
            }

            @Override
            public R next() {
                checkNext();
                R r = queue.poll();
                if (r == null) {
                    throw new NoSuchElementException();
                } else {
                    return r;
                }
            }

            private void checkNext() {
                //TODO
            }

        };
    }

}
