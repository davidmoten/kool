package org.davidmoten.kool.internal.operators.stream;

import java.util.Set;
import java.util.function.Function;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.BaseStreamIterator;

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
        return new BaseStreamIterator<T, T>(stream) {

            Set<K> set = Sets.newHashSet();

            @Override
            public void dispose() {
                super.dispose();
                set = null;
            }

            @Override
            public void load() {
                // it != null and next == null
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
        };
    }

}
