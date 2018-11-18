package org.davidmoten.kool.internal.operators.stream;

import java.util.function.Predicate;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.StreamUtils;

import com.github.davidmoten.guavamini.Preconditions;

public final class Any<T> implements Single<Boolean> {

    private final Stream<T> stream;
    private final Predicate<? super T> predicate;

    public Any(Stream<T> stream, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(stream);
        Preconditions.checkNotNull(predicate);
        this.stream = stream;
        this.predicate = predicate;
    }

    @Override
    public Boolean get() {
        StreamIterator<T> it = StreamUtils.iterator(stream);
        try {
            while (it.hasNext()) {
                if (predicate.test(StreamUtils.next(it))) {
                    return true;
                }
            }
            return false;
        } finally {
            it.dispose();
        }
    }

}
