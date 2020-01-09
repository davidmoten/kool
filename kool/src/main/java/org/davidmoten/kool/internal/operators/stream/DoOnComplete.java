package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Action;

public final class DoOnComplete<T> implements Stream<T> {

    private final Action action;
    private final Stream<T> source;

    public DoOnComplete(Action action, Stream<T> source) {
        this.action = action;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            final StreamIterator<T> it = source.iteratorNullChecked();
            boolean actionRun = false;

            @Override
            public boolean hasNext() {
                boolean r = it.hasNext();
                if (!r && !actionRun) {
                    actionRun = true;
                    action.callUnchecked();
                    it.dispose();
                }
                return r;
            }

            @Override
            public T next() {
                return it.nextNullChecked();
            }

            @Override
            public void dispose() {
                it.dispose();
            }

        };
    }

}
