package org.davidmoten.kool.internal.operators.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.RingBuffer;

import com.github.davidmoten.guavamini.Preconditions;

public class Buffer<T> implements Stream<List<T>> {

    private final Stream<T> stream;
    private final int size;
    private final int step;
    private final boolean copy;

    public Buffer(Stream<T> stream, int size, int step, boolean copy) {
        Preconditions.checkArgument(step > 0, "step must be greater than 0");
        this.stream = stream;
        this.size = size;
        this.step = step;
        this.copy = copy;
    }

    @Override
    public StreamIterator<List<T>> iterator() {
        return new StreamIterator<List<T>>() {

            StreamIterator<T> it = stream.iteratorNullChecked();
            RingBuffer<T> buffer = new RingBuffer<>(size);
            boolean applyStep = false;

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
                    applyStep = true;
                    if (copy) {
                        return new ArrayList<>(buffer);
                    } else {
                        return buffer;
                    }
                }
            }

            @Override
            public void dispose() {
                it.dispose();
            }

            private void loadNext() {
                if (applyStep) {
                    int n = Math.min(step, buffer.size());
                    for (int i = 0; i < n; i++) {
                        buffer.poll();
                    }
                    applyStep = false;
                }
                while (buffer.size() < size && it.hasNext()) {
                    buffer.add(it.nextNullChecked());
                }
            }
        };
    }

}
