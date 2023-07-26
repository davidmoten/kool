package org.davidmoten.kool.internal.util;

import org.davidmoten.kool.exceptions.BufferOverflowException;

public final class RingBuffer<T> {

    private T[] buffer;
    private int start;
    private int finish;

    public RingBuffer() {
        this(1000);
    }

    @SuppressWarnings("unchecked")
    public RingBuffer(int maxSize) {
        // add one element to array to differentiate between a full RingBuffer and an
        // empty one
        this.buffer = (T[]) new Object[maxSize + 1];
        this.start = 0;
        this.finish = 0;
    }

    public RingBuffer<T> add(T value) {
        buffer[finish] = value;
        finish = (finish + 1) % buffer.length;
        if (finish == start) {
            throw new BufferOverflowException("buffer overflowed, maxSize="+ (buffer.length));
        }
        return this;
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
        if (count > buffer.length - 1) {
            throw new IllegalArgumentException("cannot replay " + count + " items on a buffer of size="+ (buffer.length - 1));
        }
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
