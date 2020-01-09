package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class Range implements Stream<Integer> {

    private final int start;
    private final int length;

    public Range(int start, int length) {
        this.start = start;
        this.length = length;
    }

    @Override
    public StreamIterator<Integer> iterator() {
        return new StreamIterator<Integer>() {

            int i = start;

            @Override
            public boolean hasNext() {
                return i - start < length;
            }

            @Override
            public  Integer next() {
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
