package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Predicate;

import com.github.davidmoten.guavamini.Preconditions;

public final class All<T> implements Single<Boolean> {

    private final Stream<T> stream;
    private final Predicate<? super T> predicate;

    public All(Stream<T> stream, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(stream);
        Preconditions.checkNotNull(predicate);
        this.stream = stream;
        this.predicate = predicate;
    }

    @Override
    public Boolean get() {
        StreamIterator<T> it = stream.iteratorChecked();
        try {
            while (it.hasNext()) {
                if (!predicate.testChecked(it.nextChecked())) {
                    return false;
                }
            }
            return true;
        } finally {
            it.dispose();
        }

    }

}
