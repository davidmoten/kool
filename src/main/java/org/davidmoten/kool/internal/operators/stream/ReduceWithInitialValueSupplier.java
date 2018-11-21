package org.davidmoten.kool.internal.operators.stream;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class ReduceWithInitialValueSupplier<R, T> implements Single<R> {

    private final Supplier<R> initialValue;
    private final BiFunction<? super R, ? super T, ? extends R> reducer;
    private final StreamIterable<T> source;

    public ReduceWithInitialValueSupplier(Supplier<R> initialValue,
            BiFunction<? super R, ? super T, ? extends R> reducer, StreamIterable<T> source) {
        this.initialValue = initialValue;
        this.reducer = reducer;
        this.source = source;
    }

    @Override
    public R get() {
        StreamIterator<T> it = null;
        try {
            it = Preconditions.checkNotNull(source.iterator());
            R r = Preconditions.checkNotNull(initialValue.get());
            while (it.hasNext()) {
                r = Preconditions.checkNotNull(reducer.apply(r, it.next()));
            }
            return r;
        } finally {
            if (it != null) {
                it.dispose();
            }
        }
    }

}
