package org.davidmoten.kool;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

public final class Publisher<T> implements Stream<T> {

    private final StreamIterable<T> stream;
    private final Queue<T> queue;

    public Publisher(StreamIterable<T> stream) {
        this.stream = stream;
        this.queue = new ArrayDeque<>();
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = stream.iteratorNullChecked();
            boolean itHasNext = true;
            boolean disposed = false;

            @Override
            public boolean hasNext() {
                load();
                if (itHasNext) {
                    return true;
                } else {
                    return !queue.isEmpty();
                }
            }

            @Override
            public T next() {
                load();
                if (itHasNext) {
                    return it.next();
                } else if (queue.isEmpty()) {
                    throw new NoSuchElementException();
                } else {
                    return queue.poll();
                }
            }

            @Override
            public void dispose() {
                if (!disposed) {
                    it.dispose();
                }
                disposed = true;
            }

            private void load() {
                if (itHasNext) {
                    itHasNext = it.hasNext();
                    if (!itHasNext) {
                        it.dispose();
                    }
                }
            }
        };
    }

    public void onNext(T value) {
        queue.add(value);
    }

}
