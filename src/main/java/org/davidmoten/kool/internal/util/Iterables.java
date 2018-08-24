package org.davidmoten.kool.internal.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Iterables {

    private Iterables() {
        // prevent instantiation
    }

    public static <T> Iterable<T> fromArray(T[] array) {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    int i = 0;

                    @Override
                    public boolean hasNext() {
                        return i < array.length;
                    }

                    @Override
                    public T next() {
                        i++;
                        if (i > array.length) {
                            throw new NoSuchElementException();
                        } else {
                            return array[i - 1];
                        }
                    }
                };
            }
        };
    }
    
}
