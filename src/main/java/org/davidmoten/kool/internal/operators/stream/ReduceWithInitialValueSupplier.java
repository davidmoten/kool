package org.davidmoten.kool.internal.operators.stream;


import java.util.concurrent.Callable;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.BiFunction;
import org.davidmoten.kool.internal.util.Exceptions;

import com.github.davidmoten.guavamini.Preconditions;

public final class ReduceWithInitialValueSupplier<R, T> implements Single<R> {

    private final Callable<R> initialValue;
    private final BiFunction<? super R, ? super T, ? extends R> reducer;
    private final StreamIterable<T> source;

    public ReduceWithInitialValueSupplier(Callable<R> initialValue,
            BiFunction<? super R, ? super T, ? extends R> reducer, StreamIterable<T> source) {
        this.initialValue = initialValue;
        this.reducer = reducer;
        this.source = source;
    }

    @Override
    public R get() {
        StreamIterator<T> it = null;
        try {
            it = source.iteratorNullChecked();
            R r = Preconditions.checkNotNull(initialValue.call());
            while (it.hasNext()) {
                r = Preconditions.checkNotNull(reducer.apply(r, it.next()));
            }
            return r;
        } catch (Exception e) {
            return Exceptions.rethrow(e);
        } finally {
            if (it != null) {
                it.dispose();
            }
        }
    }

}
