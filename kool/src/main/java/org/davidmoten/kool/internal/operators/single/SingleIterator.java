package org.davidmoten.kool.internal.operators.single;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.StreamIterator;

public final class SingleIterator<T> implements StreamIterator<T> {

    private final Single<T> single;
    private T value;
    private boolean finished;

    public SingleIterator(Single<T> single) {
        this.single = single;
    }

    @Override
    public boolean hasNext() {
        load();
        return !finished;
    }

    @Override
    public T next() {
        load();
        if (finished) {
            throw new RuntimeException("stream finished");
        } else {
            T v = value;
            dispose();
            return v;
        }
    }

    @Override
    public void dispose() {
        value = null;
        finished = true;
    }
    
    private void load() {
        if (!finished) {
            value = single.get();
        }
    }

}
