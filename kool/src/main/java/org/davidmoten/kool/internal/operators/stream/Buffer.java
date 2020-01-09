package org.davidmoten.kool.internal.operators.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public class Buffer<T> implements Stream<java.util.List<T>> {

    private final Stream<T> stream;
    private final int size;
    private final int step;

    public Buffer(Stream<T> stream, int size, int step) {
        this.stream = stream;
        this.size = size;
        this.step = step;
    }

    @Override
    public StreamIterator<List<T>> iterator() {
        return new StreamIterator<List<T>>() {

            StreamIterator<T> it = stream.iteratorNullChecked();
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
                    buffer = new ArrayList<>(
                            buffer.subList(Math.min(step, buffer.size()), buffer.size()));
                    return list;
                }
            }

            @Override
            public void dispose() {
                it.dispose();
            }

            private void loadNext() {
                while (buffer.size() < size && it.hasNext()) {
                    buffer.add(it.nextNullChecked());
                }
            }
        };
    }

}
