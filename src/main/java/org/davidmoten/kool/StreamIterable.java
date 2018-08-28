package org.davidmoten.kool;

public interface StreamIterable<T> extends Iterable<T>{

    @Override
    StreamIterator<T> iterator();
    
}
