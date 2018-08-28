package org.davidmoten.kool.internal.operators;

import java.util.Iterator;

import org.davidmoten.kool.Stream;

public class DoOnComplete<T> implements Stream<T> {

    private final Runnable action;
    private final Stream<T> source;

    public DoOnComplete(Runnable action, Stream<T> source) {
        this.action = action;
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Iterator<T> it = source.iterator();
            boolean completed = false;
            
            @Override
            public boolean hasNext() {
                boolean r = it.hasNext();
                if (!r && !completed) {
                    completed = true;
                    action.run();
                }
                return r;
            }

            @Override
            public T next() {
                return it.next();
            }
            
        };
    }

}
