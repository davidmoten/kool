package org.davidmoten.kool.internal.operators;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

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

            final StreamIterator<T> it = Preconditions.checkNotNull(source.iterator());
            boolean completed = false;

            @Override
            public boolean hasNext() {
                boolean r = it.hasNext();
                if (!r && !completed) {
                    completed = true;
                    action.run();
                    it.dispose();
                }
                return r;
            }

            @Override
            public T next() {
                return Preconditions.checkNotNull(it.next());
            }

            @Override
            public void dispose() {
                it.dispose();
            }

        };
    }

}
