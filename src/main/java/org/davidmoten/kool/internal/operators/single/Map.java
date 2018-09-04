package org.davidmoten.kool.internal.operators.single;

import java.util.NoSuchElementException;
import java.util.function.Function;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class Map<T, R> implements Single<R> {

    private final Function<? super T, ? extends R> mapper;
    private final Single<T> source;

    public Map(Function<? super T, ? extends R> mapper, Single<T> source) {
        this.mapper = mapper;
        this.source = source;

    }

    @Override
    public StreamIterator<R> iterator() {
        return new StreamIterator<R>() {

            StreamIterator<T> it = Preconditions.checkNotNull(source.iterator());
            T value;

            @Override
            public boolean hasNext() {
                loadValue();
                return value != null;
            }

            @Override
            public R next() {
                loadValue();
                if (value == null) {
                    throw new NoSuchElementException();
                } else {
                    T v = value;
                    it.dispose();
                    it = null;
                    value = null;
                    return Preconditions.checkNotNull(mapper.apply(v));
                }
            }

            private void loadValue() {
                if (it != null && value == null) {
                    if (it.hasNext()) {
                        value = Preconditions.checkNotNull(it.next());
                    }
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    it = null;
                    value = null;
                }
            }

        };
    }

    @Override
    public R value() {
        return source.value();
    }

}
