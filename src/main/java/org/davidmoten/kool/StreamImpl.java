package org.davidmoten.kool;

import java.util.Iterator;

public class StreamImpl<T> implements Stream<T> {

    private final Iterable<T> source;

    public StreamImpl(Iterable<T> source) {
        this.source = source;
    }

    @Override
    public Iterator<T> iterator() {
        return source.iterator();
    }

}
