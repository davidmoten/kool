package org.davidmoten.kool.internal.operators.stream;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class Reverse<T> implements Stream<T> {

    private final Stream<T> stream;

    public Reverse(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = stream.iteratorNullChecked();
            final LinkedList<T> list = new LinkedList<>();

            @Override
            public boolean hasNext() {
                load();
                return !list.isEmpty();
            }

            @Override
            public T next() {
                load();
                if (list.isEmpty()) {
                    throw new NoSuchElementException();
                } else {
                    return list.removeLast();
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    it = null;
                    list.clear();
                }
            }

            private void load() {
                if (it != null) {
                    while (it.hasNext()) {
                        list.add(it.nextNullChecked());
                    }
                    it.dispose();
                    it = null;
                }
            }
        };
    }

}
