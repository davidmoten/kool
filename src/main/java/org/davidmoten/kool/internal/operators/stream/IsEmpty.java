package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class IsEmpty implements Single<Boolean> {

    private final Stream<?> source;

    public IsEmpty(Stream<?> source) {
        this.source = source;
    }

    @Override
    public Boolean get() {
        StreamIterator<?> it = Preconditions.checkNotNull(source.iterator());
        boolean hasNext = it.hasNext();
        it.dispose();
        return !hasNext;
    }

}
