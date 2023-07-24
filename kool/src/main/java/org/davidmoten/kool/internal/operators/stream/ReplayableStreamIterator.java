package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.RingBuffer;

import com.github.davidmoten.guavamini.Preconditions;

public final class ReplayableStreamIterator<T> implements StreamIterator<T> {

    private final StreamIterator<T> it;
    private final int maxReplay;
    private final RingBuffer<T> buffer;

    public ReplayableStreamIterator(StreamIterator<T> it, int maxReplay) {
        this.it = it;
        this.maxReplay = maxReplay;
        this.buffer = new RingBuffer<T>(maxReplay);
    }

    @Override
    public boolean hasNext() {
        return !buffer.isEmpty() || it.hasNext();
    }

    @Override
    public T next() {
        if (buffer.isEmpty()) {
            buffer.add(it.next());
        }
        return buffer.poll();
    }

    @Override
    public void dispose() {
        it.dispose();
    }

    public void replay(int count) {
        Preconditions.checkArgument(count + buffer.size() <= maxReplay,
                "total replay count too large: " + count + ", max is " + maxReplay);
        buffer.replay(count);
    }

    public int maxReplay() {
        return maxReplay;
    }

}
