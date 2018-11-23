package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Action;

public final class DoOnStart<T> implements Stream<T> {

    private final Stream<T> stream;
    private final Action action;

    public DoOnStart(Stream<T> stream, Action action) {
        this.stream = stream;
        this.action = action;
    }

    @Override
    public StreamIterator<T> iterator() {
        action.callUnchecked();
        return stream.iterator();
    }

}
