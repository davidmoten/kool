package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Range implements Iterable<Long> {

    private final long start;
    private final long length;

    public Range(long start, long length) {
        this.start = start;
        this.length = length;
    }

    @Override
    public Iterator<Long> iterator() {
        return new Iterator<Long>() {

            long i = start;

            @Override
            public boolean hasNext() {
                return i - start < length;
            }

            @Override
            public Long next() {
                if (i - start < length) {
                    return i++;
                } else {
                    throw new NoSuchElementException();
                }
            }

        };
    }

}
