package org.davidmoten.kool.internal.util;

import java.util.Enumeration;
import java.util.LinkedList;

public final class EvictingQueue<T> implements Enumeration<T> {

    private final LinkedList<T> list;
    private final long maxSize;

    public EvictingQueue(long maxSize) {
        list = new LinkedList<T>();
        this.maxSize = maxSize;
    }

    public void add(T value) {
        if (list.size() == maxSize) {
            list.removeFirst();
        }
        list.add(value);
    }

    @Override
    public boolean hasMoreElements() {
        return !list.isEmpty();
    }

    @Override
    public T nextElement() {
        return list.removeFirst();
    }

}
