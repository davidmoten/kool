package org.davidmoten.kool.internal.util;

import java.util.NoSuchElementException;

import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

public abstract class BaseStreamIterator<R, T> implements StreamIterator<T> {

    protected T next;
    protected StreamIterator<R> it;

    public BaseStreamIterator(StreamIterable<R> stream) {
        this.it = init(stream);
    }

    public StreamIterator<R> init(StreamIterable<R> stream) {
        return stream.iteratorChecked();
    }

    @Override
    public final boolean hasNext() {
        loadNext();
        // don't put in check on it = null
        boolean result = next != null;
        if (!result) {
            dispose();
        }
        return result;
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
