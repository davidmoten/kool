package org.davidmoten.kool.internal.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Maybe;

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
    
    @SafeVarargs
    public static <T> Iterable<T> ofNoCopy(T... values) {
        return new Iterable<T>() {
            
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    int i = 0;
                    
                    @Override
                    public boolean hasNext() {
                        return i < values.length;
                    }

                    @Override
                    public T next() {
                        if (i >= values.length) {
                            throw new NoSuchElementException();
                        } else {
                            T t = values[i];
                            i++;
                            return t;
                        }
                    }
                    
                };
            }
            
        };
    }
    
    public static <T> Maybe<T> first(Iterator<T> it) {
        if (it.hasNext()) {
            return Maybe.of(it.next());
        } else {
            return Maybe.empty();
        }
    }
    
}
