package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class Count implements Single<Long> {

    private final Stream<?> stream;

    public Count(Stream<?> stream) {
        this.stream = stream;
    }

    @Override
    public Long get() {
        StreamIterator<?> it = stream.iteratorNullChecked();
        long count = 0;
        try {
            while (it.hasNext()) {
                count++;
                it.nextNullChecked();
            }
        } finally {
            it.dispose();
        }
        return count;
    }

}
