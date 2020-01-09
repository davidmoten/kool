package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class RepeatLast<T> implements Stream<T> {

    private final Stream<T> stream;
    private final long count;

    public RepeatLast(Stream<T> stream, long count) {
        Preconditions.checkArgument(count > 0);
        this.stream = stream;
        this.count = count;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = stream.iteratorNullChecked();
            T next;
            long repeats = count;
            boolean loadNext = true;

            @Override
            public boolean hasNext() {
                load();
                return next != null && repeats > 0;
            }

            @Override
            public T next() {
                load();
                if (next == null || repeats == 0) {
                    dispose();
                    throw new NoSuchElementException();
                } else {
                    if (it == null) {
                        repeats--;
                    }
                    loadNext = true;
                    return next;
                }
            }

            private void load() {
                if (it != null && loadNext) {
                    if (it.hasNext()) {
                        next = it.nextNullChecked();
                        loadNext = false;
                    } else {
                        it.dispose();
                        it = null;
                    }
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    next = null;
                    repeats = 0;
                }
            }

        };
    }

}
