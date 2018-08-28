package org.davidmoten.kool.internal.operators;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public class DoOnComplete<T> implements Stream<T> {

    private final Runnable action;
    private final Stream<T> source;

    public DoOnComplete(Runnable action, Stream<T> source) {
        this.action = action;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            final StreamIterator<T> it = source.iterator();
            boolean completed = false;

            @Override
            public boolean hasNext() {
                boolean r = it.hasNext();
                if (!r && !completed) {
                    completed = true;
                    action.run();
                    it.cancel();
                }
                return r;
            }

            @Override
            public T next() {
                return it.next();
            }

            @Override
            public void cancel() {
                it.cancel();
            }

        };
    }

}
