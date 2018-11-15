package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class FromArrayInt implements Stream<Integer> {

    private final int[] array;
    private final int fromIndex;
    private final int toIndex;

    public FromArrayInt(int[] array, int fromIndex, int toIndex) {
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public StreamIterator<Integer> iterator() {
        return new StreamIterator<Integer>() {

            int i = fromIndex;

            @Override
            public boolean hasNext() {
                return i <= toIndex;
            }

            @Override
            public Integer next() {
                return array[i++];
            }

            @Override
            public void dispose() {
                // do nothing
            }

        };
    }

}
