package org.davidmoten.kool.internal.operators.stream;

import java.util.Optional;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.BiFunction;
import org.davidmoten.kool.internal.util.Exceptions;

import com.github.davidmoten.guavamini.Preconditions;

public final class ReduceNoInitialValue<T> implements Maybe<T> {

    private final BiFunction<? super T, ? super T, ? extends T> reducer;
    private final StreamIterable<T> source;

    public ReduceNoInitialValue(BiFunction<? super T, ? super T, ? extends T> reducer, StreamIterable<T> source) {
        this.reducer = reducer;
        this.source = source;
    }

    @Override
    public Optional<T> get() {
        StreamIterator<T> it = source.iteratorChecked();
        T a, b;
        if (it.hasNext()) {
            a = it.nextChecked();
        } else {
            return Optional.empty();
        }
        if (it.hasNext()) {
            b = it.nextChecked();
        } else {
            return Optional.empty();
        }
        T v;
        try {
            v = reducer.apply(a, b);
        } catch (Exception e) {
            return Exceptions.rethrow(e);
        }
        while (it.hasNext()) {
            try {
                v = Preconditions.checkNotNull(reducer.apply(v, it.nextChecked()));
            } catch (Exception e) {
                return Exceptions.rethrow(e);
            }
        }
        return Optional.of(v);
    }

}
