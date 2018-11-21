package org.davidmoten.kool.internal.util;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public abstract class BaseStreamIterator<R, T> implements StreamIterator<T> {

    protected T next;
    protected StreamIterator<R> it;

    public BaseStreamIterator(Stream<R> stream) {
        this.it = stream.iteratorChecked();
    }

    @Override
    public final boolean hasNext() {
        loadNext();
        return next != null;
    }

    @Override
    public final T next() {
        loadNext();
        T v = next;
        if (v != null) {
            next = null;
            return v;
        } else {
            throw new NoSuchElementException();
        }

    }

    private void loadNext() {
        if (it != null && next == null) {
            load();
        }
    }

    @Override
    public void dispose() {
        if (it != null) {
            it.dispose();
            it = null;
            next = null;
        }
    }
    
    /**
     * Guaranteed preconditions are that it != null and next == null.
     */
    public abstract void load();

}
