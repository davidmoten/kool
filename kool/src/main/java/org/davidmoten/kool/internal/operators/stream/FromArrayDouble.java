package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class FromArrayDouble implements Stream<Double> {

    private final double[] array;
    private final int fromIndex;
    private final int toIndex;

    public FromArrayDouble(double[] array, int fromIndex, int toIndex) {
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public StreamIterator<Double> iterator() {
        return new StreamIterator<Double>() {

            int i = fromIndex;

            @Override
            public boolean hasNext() {
                return i < toIndex;
            }

            @Override
            public Double next() {
                return array[i++];
            }

            @Override
            public void dispose() {
                // do nothing
            }

        };
    }
}
