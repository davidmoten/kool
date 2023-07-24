package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class ReplayableStream<T> implements Stream<T> {

    private final Stream<T> stream;

    public ReplayableStream(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public ReplayableStreamIterator<T> iterator() {
        StreamIterator<T> it = stream.iterator();
        return new ReplayableStreamIterator<>(it);
    }

}
