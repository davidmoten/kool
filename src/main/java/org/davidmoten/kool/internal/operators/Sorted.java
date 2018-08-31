package org.davidmoten.kool.internal.operators;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class Sorted<T> implements Stream<T> {

    private final Comparator<? super T> comparator;
    private final Stream<T> source;

    public Sorted(Comparator<? super T> comparator, Stream<T> source) {
        this.comparator = comparator;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = Preconditions.checkNotNull(source.iterator());
            Iterator<T> reversed;
            
            @Override
            public boolean hasNext() {
                calculate();
                return reversed != null;
            }

            @Override
            public T next() {
                calculate();
                if (it == null) {
                    throw new NoSuchElementException();
                } else {
                    if (reversed.hasNext()) {
                        return reversed.next();
                    } else {
                        throw new NoSuchElementException():
                    }
                }
            }

            @Override
            public void dispose() {
                
            }
            
            private void calculate() {
                if (it != null) {
                    
                }
            }
            
        };
    }

    
    
}
