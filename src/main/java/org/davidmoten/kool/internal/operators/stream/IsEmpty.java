package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class IsEmpty implements Stream<Boolean> {

    private final Stream<?> source;

    public IsEmpty(Stream<?> source) {
        this.source = source;
    }

    @Override
    public StreamIterator<Boolean> iterator() {
        return new StreamIterator<Boolean>() {

            StreamIterator<?> it = Preconditions.checkNotNull(source.iterator());
            Boolean empty;

            @Override
            public boolean hasNext() {
                return it != null;
            }

            @Override
            public Boolean next() {
                if (empty == null) {
                    empty = !it.hasNext();
                    it.dispose();
                    it = null;
                    return empty;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    it = null;
                }
            }

        };
    }

}
