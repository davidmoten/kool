package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class Count implements Single<Long> {

    private final Stream<?> stream;

    public Count(Stream<?> stream) {
        this.stream = stream;
    }

    @Override
    public Long get() {
        StreamIterator<?> it = stream.iterator();
        long count = 0;
        try {
            while (it.hasNext()) {
                count++;
                Preconditions.checkNotNull(it.next());
            }
        } finally {
            it.dispose();
        }
        return count;
    }

}
