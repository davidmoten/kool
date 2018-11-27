package org.davidmoten.kool.internal.operators.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class Sorted<T> implements Stream<T> {

    private final Comparator<? super T> comparator;
    private final Stream<T> source;

    private static final List<Object> INITIALIZED = new ArrayList<>();

    public Sorted(Comparator<? super T> comparator, Stream<T> source) {
        this.comparator = comparator;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            @SuppressWarnings("unchecked")
            List<T> list = (List<T>) INITIALIZED;
            int i = 0;

            @Override
            public boolean hasNext() {
                calculate();
                return list != null && i < list.size();
            }

            @Override
            public T next() {
                calculate();
                if (list == null || i == list.size()) {
                    throw new NoSuchElementException();
                } else {
                    T t = list.get(i);
                    i++;
                    if (i == list.size()) {
                        list = null;
                    }
                    return t;
                }
            }

            @Override
            public void dispose() {
                list = null;
            }

            private void calculate() {
                if (list == INITIALIZED) {
                    list = source.toList().get();
                    Collections.sort(list, comparator);
                }
            }

        };
    }

}
