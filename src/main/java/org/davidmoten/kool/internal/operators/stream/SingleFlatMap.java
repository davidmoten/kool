package org.davidmoten.kool.internal.operators.stream;

import java.util.function.Function;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public class SingleFlatMap<T, R> implements Stream<R> {

    private final Function<? super T, ? extends Stream<? extends R>> mapper;
    private final Single<T> single;

    public SingleFlatMap(Single<T> single, Function<? super T, ? extends Stream<? extends R>> mapper) {
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
                return it.next();
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
                    it = mapper.apply(single.get()).iterator();
                }
            }

        };
    }

}
