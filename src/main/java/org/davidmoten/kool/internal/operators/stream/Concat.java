package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

public final class Concat<T> implements Stream<T> {

    private final StreamIterable<? extends T> source1;
    private final StreamIterable<? extends T> source2;

    public Concat(StreamIterable<? extends T> source1, StreamIterable<? extends T> source2) {
        this.source1 = source1;
        this.source2 = source2;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<? extends T> a = source1.iteratorChecked();
            StreamIterator<? extends T> b = null;

            @Override
            public boolean hasNext() {
                if (a != null) {
                    if (a.hasNext()) {
                        return true;
                    } else {
                        // release a for gc
                        a.dispose();
                        a = null;
                        return b().hasNext();
                    }
                } else {
                    return b().hasNext();
                }
            }

            @Override
            public T next() {
                if (a != null && a.hasNext()) {
                    return a.nextChecked();
                } else if (b().hasNext()) {
                    return b().nextChecked();
                } else {
                    dispose();
                    throw new NoSuchElementException();
                }
            }

            private StreamIterator<? extends T> b() {
                if (b == null) {
                    b = source2.iteratorChecked();
                }
                return b;
            }

            @Override
            public void dispose() {
                if (a != null) {
                    a.dispose();
                }
                if (b != null) {
                    b.dispose();
                }
            }

        };
    }

}
