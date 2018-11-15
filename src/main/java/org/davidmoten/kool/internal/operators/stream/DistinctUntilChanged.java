package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;
import java.util.function.Function;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class DistinctUntilChanged<T, K> implements Stream<T> {

    private final Stream<T> stream;
    private final Function<? super T, K> keySelector;

    public DistinctUntilChanged(Stream<T> stream, Function<? super T, K> keySelector) {
        this.stream = stream;
        this.keySelector = keySelector;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = Preconditions.checkNotNull(stream.iterator());
            K key;
            T next;

            @Override
            public boolean hasNext() {
                load();
                return next != null;
            }

            @Override
            public T next() {
                load();
                T v = next;
                if (v != null) {
                    next = null;
                    return v;
                } else {
                    throw new NoSuchElementException();
                }
            }

            private void load() {
                if (it != null && next == null) {
                    while (it.hasNext()) {
                        T v = Preconditions.checkNotNull(it.next());
                        K k = Preconditions.checkNotNull(keySelector.apply(v));
                        if (!k.equals(key)) {
                            key = k;
                            next = v;
                            break;
                        }
                    }
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    next = null;
                    key = null;
                    it = null;
                }
            }

        };
    }

}
