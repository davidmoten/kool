package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.RingBuffer;

public final class SkipLast<T> implements Stream<T> {

    private final Stream<T> stream;
    private final int size;

    public SkipLast(int size, Stream<T> stream) {
        this.stream = stream;
        this.size = size;
    }

    @Override
    public StreamIterator<T> iterator() {
        RingBuffer<T> buffer = new RingBuffer<T>(size + 1);
        StreamIterator<T> it = stream.iterator();
        return new StreamIterator<T>() {

            @Override
            public boolean hasNext() {
                loadNext();
                return buffer.size() == size + 1;
            }

            @Override
            public T next() {
                loadNext();
                if (buffer.size() == size + 1) {
                    return buffer.poll();
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void dispose() {
                it.dispose();
            }

            private void loadNext() {
                while (buffer.size() < size + 1 && it.hasNext()) {
                    buffer.offer(it.next());
                }
            }
        };
    }
}
