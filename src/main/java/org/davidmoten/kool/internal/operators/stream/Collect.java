package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class Collect<T, R> implements Stream<R> {

    private final Supplier<? extends R> factory;
    private final BiConsumer<? super R, ? super T> collector;
    private final Stream<T> source;

    public Collect(Supplier<? extends R> factory, BiConsumer<? super R, ? super T> collector,
            Stream<T> source) {
        this.factory = factory;
        this.collector = collector;
        this.source = source;
    }

    @Override
    public StreamIterator<R> iterator() {
        return new StreamIterator<R>() {

            StreamIterator<T> it = Preconditions.checkNotNull(source.iterator());
            R value;

            @Override
            public boolean hasNext() {
                load();
                return value != null;
            }

            @Override
            public R next() {
                load();
                if (value == null) {
                    throw new NoSuchElementException();
                } else {
                    R t = value;
                    value = null;
                    it = null;
                    return t;
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                }
            }

            private void load() {
                if (value == null && it != null) {
                    R c = factory.get();
                    while (it.hasNext()) {
                        collector.accept(c, Preconditions.checkNotNull(it.next()));
                    }
                    it.dispose();
                    value = c;
                }
            }
        };
    }

}
