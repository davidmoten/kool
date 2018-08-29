package org.davidmoten.kool.internal.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class Buffer<T> implements Stream<List<T>> {

    private final int size;
    private final Stream<T> source;

    public Buffer(int size, Stream<T> source) {
        this.size = size;
        this.source = source;
    }

    @Override
    public StreamIterator<List<T>> iterator() {
        return new StreamIterator<List<T>>() {

            StreamIterator<T> it = Preconditions.checkNotNull(source.iterator());
            List<T> buffer = new ArrayList<>(size);

            @Override
            public boolean hasNext() {
                loadNext();
                return !buffer.isEmpty();
            }

            @Override
            public List<T> next() {
                loadNext();
                if (buffer.isEmpty()) {
                    throw new NoSuchElementException();
                } else {
                    List<T> list = buffer;
                    buffer = new ArrayList<>();
                    return list;
                }
            }

            @Override
            public void cancel() {
                it.cancel();
            }

            private void loadNext() {
                while (buffer.size() < size && it.hasNext()) {
                    T t = it.next();
                    buffer.add(t);
                }
            }
        };
    }

}
