package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class DoOnEmpty<T> implements Stream<T> {

    private final Stream<T> stream;
    private final Runnable action;

    public DoOnEmpty(Stream<T> stream, Runnable action) {
        this.stream = stream;
        this.action = action;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = stream.iteratorChecked();
            boolean checkedForEmpty;

            @Override
            public boolean hasNext() {
                check();
                return it == null || it.hasNext();
            }

            @Override
            public T next() {
                check();
                if (it == null) {
                    throw new NoSuchElementException();
                } else {
                    return it.nextChecked();
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    // release for gc
                    it = null;
                }
            }

            private void check() {
                if (!checkedForEmpty) {
                    if (!it.hasNext()) {
                        action.run();
                    }
                    checkedForEmpty = true;
                }
            }

        };
    }

}
