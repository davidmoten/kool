package org.davidmoten.kool.internal.operators.maybe;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.internal.util.EmptyStream;

import com.github.davidmoten.guavamini.Preconditions;

public final class MaybeFlatMap<T, R> implements Stream<R> {

    private final Maybe<T> maybe;
    private final Function<? super T, ? extends StreamIterable<? extends R>> mapper;

    public MaybeFlatMap(Maybe<T> maybe, Function<? super T, ? extends StreamIterable<? extends R>> mapper) {
        this.maybe = maybe;
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
                return !finished && Preconditions.checkNotNull(it.hasNext());
            }

            @Override
            public R next() {
                load();
                if (finished) {
                    throw new NoSuchElementException();
                } else {
                    return it.next();
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    it = null;
                    finished = true;
                }
            }

            @SuppressWarnings("unchecked")
            private void load() {
                if (!finished && it == null) {
                    Optional<T> v = maybe.get();
                    if (v.isPresent()) {
                        it = mapper.applyUnchecked(v.get()).iterator();
                    } else {
                        it = (StreamIterator<? extends R>) EmptyStream.INSTANCE.iterator();
                    }
                }
            }
        };
    }

}
