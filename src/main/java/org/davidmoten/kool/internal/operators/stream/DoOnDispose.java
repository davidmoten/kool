package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Action;

public final class DoOnDispose<T> implements Stream<T> {

    private final Action action;
    private final Stream<T> source;
    private final boolean before;

    public DoOnDispose(Action action, Stream<T> source, boolean before) {
        this.action = action;
        this.source = source;
        this.before = before;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = source.iteratorNullChecked();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.nextNullChecked();
            }

            @Override
            public void dispose() {
                if (it != null) {
                    if (before) {
                        action.callUnchecked();
                    }
                    it.dispose();
                    it = null;
                    if (!before) {
                        action.callUnchecked();
                    }
                }
            }

        };
    }

}
