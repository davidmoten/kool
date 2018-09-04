package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class DoOnDispose<T> implements Stream<T> {

    private final Runnable action;
    private final Stream<T> source;
    private final boolean before;

    public DoOnDispose(Runnable action, Stream<T> source, boolean before) {
        this.action = action;
        this.source = source;
        this.before = before;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = Preconditions.checkNotNull(source.iterator());

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return Preconditions.checkNotNull(it.next());
            }

            @Override
            public void dispose() {
                if (before) {
                    action.run();
                }
                it.dispose();
                if (!before) {
                    action.run();
                }
            }

        };
    }

}
