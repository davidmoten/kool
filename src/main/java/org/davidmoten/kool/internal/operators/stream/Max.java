package org.davidmoten.kool.internal.operators.stream;

import java.util.Comparator;
import java.util.Optional;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public class Max<T> implements Maybe<T> {

    private final Stream<T> stream;
    private final Comparator<? super T> comparator;
    private final boolean not;

    public Max(Stream<T> stream, Comparator<? super T> comparator, boolean not) {
        this.stream = stream;
        this.comparator = comparator;
        this.not = not;
    }

    @Override
    public Optional<T> get() {
        StreamIterator<T> it = Preconditions.checkNotNull(stream.iterator());
        try {
            T max = null;
            while (it.hasNext()) {
                T v = it.nextChecked();
                if (max == null) {
                    max = v;
                } else {
                    boolean comp = comparator.compare(max, v) < 0;
                    if (comp ^ not) {
                        max = v;
                    }
                }
            }
            return Optional.ofNullable(max);
        } finally {
            it.dispose();
        }
    }

}
