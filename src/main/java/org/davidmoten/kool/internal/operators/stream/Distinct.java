package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.guavamini.Sets;

public final class Distinct<T, K> implements Stream<T> {

    private final Stream<T> stream;
    private final Function<? super T, K> keySelector;

    public Distinct(Stream<T> stream, Function<? super T, K> keySelector) {
        Preconditions.checkNotNull(keySelector);
        this.stream = stream;
        this.keySelector = keySelector;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = stream.iteratorChecked();
            T next;
            Set<K> set = Sets.newHashSet();

            @Override
            public boolean hasNext() {
                load();
                return next != null;
            }

            @Override
            public T next() {
                load();
                if (next == null) {
                    throw new NoSuchElementException();
                } else {
                    T v = next;
                    next = null;
                    return v;
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    it = null;
                    next = null;
                    set = null;
                }
            }

            private void load() {
                if (it != null && next == null) {
                    while (it.hasNext()) {
                        T v = it.nextChecked();
                        K k = Preconditions.checkNotNull(keySelector.apply(v));
                        if (!set.contains(k)) {
                            set.add(k);
                            next = v;
                            return;
                        }
                    }
                }
            }

        };
    }

}
