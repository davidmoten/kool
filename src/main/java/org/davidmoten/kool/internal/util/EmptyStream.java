package org.davidmoten.kool.internal.util;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class EmptyStream implements Stream<Object>, StreamIterator<Object> {
    
    public static final EmptyStream INSTANCE = new EmptyStream();

    @Override
    public StreamIterator<Object> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        throw new NoSuchElementException();
    }

    @Override
    public void dispose() {
        // do nothing
    }

}
