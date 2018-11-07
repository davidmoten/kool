package org.davidmoten.kool.internal.operators.single;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public class SingleCount implements Single<Long> {

    private final Stream<?> stream;

    public SingleCount(Stream<?> stream) {
        this.stream = stream;
    }

    @Override
    public Long get() {
        StreamIterator<?> it = stream.iterator();
        long count = 0;
        try {
            while (it.hasNext()) {
                count++;
                it.next();
            }
        } finally {
            it.dispose();
        }
        return count;
    }

}
