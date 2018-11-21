package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class DoOnComplete<T> implements Stream<T> {

    private final Runnable action;
    private final Stream<T> source;

    public DoOnComplete(Runnable action, Stream<T> source) {
        this.action = action;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            final StreamIterator<T> it = source.iteratorChecked();
            boolean actionRun = false;

            @Override
            public boolean hasNext() {
                boolean r = it.hasNext();
                if (!r && !actionRun) {
                    actionRun = true;
                    action.run();
                    // TODO make standard?
                    it.dispose();
                }
                return r;
            }

            @Override
            public T next() {
                return it.nextChecked();
            }

            @Override
            public void dispose() {
                it.dispose();
            }

        };
    }

}
