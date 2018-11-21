package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class FromArrayFloat implements Stream<Float> {

    private final float[] array;
    private final int fromIndex;
    private final int toIndex;

    public FromArrayFloat(float[] array, int fromIndex, int toIndex) {
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public StreamIterator<Float> iterator() {
        return new StreamIterator<Float>() {

            int i = fromIndex;

            @Override
            public boolean hasNext() {
                return i < toIndex;
            }

            @Override
            public Float next() {
                return array[i++];
            }

            @Override
            public void dispose() {
                // do nothing
            }

        };
    }
}
