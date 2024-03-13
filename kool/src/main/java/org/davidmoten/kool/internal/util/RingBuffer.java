package org.davidmoten.kool.internal.util;

import java.util.AbstractList;
import java.util.RandomAccess;
import java.util.function.Consumer;

import org.davidmoten.kool.exceptions.BufferOverflowException;

public final class RingBuffer<T> extends AbstractList<T> implements RandomAccess {

    private T[] buffer;
    private int start;
    private int finish;

    @SuppressWarnings("unchecked")
    public RingBuffer(int maxSize) {
        // add one element to array to differentiate between a full RingBuffer and an
        // empty one
        this.buffer = (T[]) new Object[maxSize + 1];
        this.start = 0;
        this.finish = 0;
    }

    public RingBuffer<T> offer(T value) {
        buffer[finish] = value;
        finish = (finish + 1) % buffer.length;
        if (finish == start) {
            throw new BufferOverflowException("buffer overflowed, maxSize=" + (buffer.length));
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

    @Override
    public int size() {
        if (finish < start) {
            return finish + buffer.length - start;
        } else {
            return finish - start;
        }
    }

    public RingBuffer<T> replay(int count) {
        if (count >= buffer.length - size()) {
            throw new IllegalArgumentException("cannot replay " + count + " items on a buffer of maxSize="
                    + (buffer.length - 1) + " where " + size() + " items still queued for reading");
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
    
    private int arrayIndex(int index) {
        if (index < 0 || index >= size()) {
            throw new ArrayIndexOutOfBoundsException(); 
        }
        return (start + index) % buffer.length;
    }
    
    public T get(int index) {
        return buffer[arrayIndex(index)];
    }

    @Override
    public boolean add(T e) {
        offer(e);
        return true;
    }

    @Override
    public T set(int index, T element) {
        int i = arrayIndex(index);
        T v = buffer[i];
        buffer[i] = element;
        return v;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        int size = size();
        for (int i = 0; i < size; i++) {
            action.accept(get(i));
        }
    }
}
