package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class RangeLong implements Stream<Long> {

    private final long start;
    private final long length;

    public RangeLong(long start, long length) {
        this.start = start;
        this.length = length;
    }

    @Override
    public StreamIterator<Long> iterator() {
        return new StreamIterator<Long>() {

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

            @Override
            public void dispose() {
                // do nothing
            }

        };
    }

}
