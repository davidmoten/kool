package org.davidmoten.kool.internal.util;

import java.util.Iterator;

import org.davidmoten.kool.Stream;

public final class StreamImpl<T> implements Stream<T> {

    private final Iterable<T> source;

    public StreamImpl(Iterable<T> source) {
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return source.iterator();
    }

}
