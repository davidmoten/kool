package org.davidmoten.kool.internal.operators.single;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class SingleOf<T> implements Single<T> {

    private T value;

    public SingleOf(T value) {
        this.value = Preconditions.checkNotNull(value);
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {
            boolean emitted;

            @Override
            public boolean hasNext() {
                return value != null && !emitted;
            }

            @Override
            public T next() {
                if (value == null || emitted) {
                    throw new NoSuchElementException();
                } else {
                    emitted = true;
                    return value;
                }
            }

            @Override
            public void dispose() {
                // do nothing
            }

        };
    }

}
