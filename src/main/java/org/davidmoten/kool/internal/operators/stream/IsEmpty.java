package org.davidmoten.kool.internal.operators.stream;

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
            
            @Override
            public boolean hasNext() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Boolean next() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }

        };
    }

}
