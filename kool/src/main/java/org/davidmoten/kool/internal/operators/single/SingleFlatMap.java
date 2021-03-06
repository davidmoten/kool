package org.davidmoten.kool.internal.operators.single;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Function;

import com.github.davidmoten.guavamini.Preconditions;

public class SingleFlatMap<T, R> implements Stream<R> {

    private final Function<? super T, ? extends StreamIterable<? extends R>> mapper;
    private final Single<T> single;

    public SingleFlatMap(Single<T> single, Function<? super T, ? extends StreamIterable<? extends R>> mapper) {
        this.single = single;
        this.mapper = mapper;
    }

    @Override
    public StreamIterator<R> iterator() {
        return new StreamIterator<R>() {

            StreamIterator<? extends R> it;
            boolean finished;

            @Override
            public boolean hasNext() {
                load();
                return it.hasNext();
            }

            @Override
            public R next() {
                load();
                return it.nextNullChecked();
            }

            @Override
            public void dispose() {
                finished = false;
                it.dispose();
                it = null;
            }

            private void load() {
                if (finished) {
                    throw new IllegalStateException("stream finished");
                } else if (it == null) {
                    it = Preconditions.checkNotNull(mapper.applyUnchecked(single.get()).iterator());
                }
            }

        };
    }

}
