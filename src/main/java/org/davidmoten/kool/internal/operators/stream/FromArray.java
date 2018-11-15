package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public class FromArray<T> implements Stream<T> {

    private final T[] array;
    private final int fromIndex;
    private final int toIndex;

    public FromArray(T[] array, int fromIndex, int toIndex) {
        Preconditions.checkArgument(fromIndex >= 0 && fromIndex <= toIndex);
        Preconditions.checkArgument(toIndex < array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public StreamIterator<T> iterator() {

        return new StreamIterator<T>() {

            int i = fromIndex;

            @Override
            public boolean hasNext() {
                return i <= toIndex;
            }

            @Override
            public T next() {
                return Preconditions.checkNotNull(array[i++]);
            }

            @Override
            public void dispose() {
                // do nothing
            }

        };
    }

}
