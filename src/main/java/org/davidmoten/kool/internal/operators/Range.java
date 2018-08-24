package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class  Range implements Iterable<Integer> {

    private final int start;
    private final int length;

    public Range(int start, int length) {
        this.start = start;
        this.length = length;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {

            int i = start;
            
            @Override
            public boolean hasNext() {
                return i < start + length;
            }

            @Override
            public Integer next() {
                if (i < start + length) {
                    return i++;
                } else {
                    throw new NoSuchElementException();
                }
            }
            
        };
    }

}
