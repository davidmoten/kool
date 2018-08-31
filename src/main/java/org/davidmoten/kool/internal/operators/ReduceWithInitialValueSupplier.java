package org.davidmoten.kool.internal.operators;

import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class ReduceWithInitialValueSupplier<R, T> implements Stream<R> {

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
    public StreamIterator<R> iterator() {
        return new StreamIterator<R>() {

            final StreamIterator<T> it = Preconditions.checkNotNull(source.iterator());
            R value;
            boolean finished;

            @Override
            public boolean hasNext() {
                if (finished) {
                    return false;
                } else {
                    calculate();
                    return true;
                }
            }

            @Override
            public R next() {
                if (finished) {
                    throw new NoSuchElementException();
                } else {
                    finished = true;
                    calculate();
                    R r = value;
                    value = null;
                    return r;
                }
            }

            private void calculate() {
                R r = Preconditions.checkNotNull(initialValue.get());
                while (it.hasNext()) {
                    r = Preconditions.checkNotNull(reducer.apply(r, it.next()));
                }
                value = r;
            }

            @Override
            public void dispose() {
                it.dispose();
            }

        };
    }

}
