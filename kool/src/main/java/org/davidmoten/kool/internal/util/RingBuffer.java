package org.davidmoten.kool.internal.util;

public final class RingBuffer<T> {

    private T[] buffer;
    private int start;
    private int finish;
    private final int maxSize;

    private static final float GROWTH_FACTOR = 1.5f;

    public RingBuffer() {
        this(7, 1000);
    }

    @SuppressWarnings("unchecked")
    public RingBuffer(int initialSize, int maxSize) {
        // add one element to array to differentiate between a full RingBuffer and an
        // empty one
        this.buffer = (T[]) new Object[initialSize + 1];
        this.maxSize = maxSize;
        this.start = 0;
        this.finish = 0;
    }

    public RingBuffer<T> add(T value) {
        ensureArrayLargeEnough();
        buffer[finish] = value;
        finish = (finish + 1) % buffer.length;
        return this;
    }

    private void ensureArrayLargeEnough() {
        if (size() == buffer.length - 2) {
            int newLength = Math.min(maxSize + 1,
                    buffer.length + Math.max(1, Math.round(buffer.length * GROWTH_FACTOR)));
            if (newLength == buffer.length) {
                throw new RuntimeException("buffer at max size and is full, maxSize=" + maxSize);
            }
            @SuppressWarnings("unchecked")
            T[] newBuffer = (T[]) new Object[newLength];
            if (start <= finish) {
                System.arraycopy(buffer, 0, newBuffer, 0, finish);
            } else {
                // make space in the middle between finish and start
                System.arraycopy(buffer, 0, newBuffer, 0, finish);
                int extra = newLength - buffer.length;
                System.arraycopy(buffer, finish, newBuffer, finish + extra, buffer.length - finish);
                start = newBuffer.length - buffer.length + start;
            }
            buffer = newBuffer;
        }
    }

    public T poll() {
        if (start == finish) {
            return null;
        } else {
            T value = buffer[start];
            start = (start + 1) % buffer.length;
            return value;
        }
    }

    public int size() {
        if (finish < start) {
            return finish + buffer.length - start;
        } else {
            return finish - start;
        }
    }

    public RingBuffer<T> replay(int count) {
        start = start - count;
        if (start < 0) {
            start += buffer.length;
        }
        return this;
    }

    public boolean isEmpty() {
        return start == finish;
    }

}
