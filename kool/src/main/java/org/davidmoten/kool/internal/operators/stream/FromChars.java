package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class FromChars implements Stream<Integer> {

    private final CharSequence chars;
    private final int fromIndex;
    private final int toIndex;

    public FromChars(CharSequence chars, int fromIndex, int toIndex) {
        this.chars = chars;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public StreamIterator<Integer> iterator() {
        return new StreamIterator<Integer>() {

            int i = fromIndex;

            @Override
            public boolean hasNext() {
                return i < toIndex;
            }

            @Override
            public Integer next() {
                return (int) chars.charAt(i++);
            }

            @Override
            public void dispose() {
                // do nothing
            }

        };
    }

}
