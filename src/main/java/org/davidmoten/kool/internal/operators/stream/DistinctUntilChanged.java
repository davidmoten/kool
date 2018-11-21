package org.davidmoten.kool.internal.operators.stream;

import java.util.function.Function;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.BaseStreamIterator;

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
        return new BaseStreamIterator<T, T>(stream) {

            K key;

            @Override
            public void load() {
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

            @Override
            public void dispose() {
                super.dispose();
                key = null;
            }

        };
    }

}
