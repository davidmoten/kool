package org.davidmoten.kool.internal.operators.stream;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class Collect<T, R> implements Single<R> {

    private final Supplier<? extends R> factory;
    private final BiConsumer<? super R, ? super T> collector;
    private final Stream<T> source;

    public Collect(Supplier<? extends R> factory, BiConsumer<? super R, ? super T> collector, Stream<T> source) {
        this.factory = factory;
        this.collector = collector;
        this.source = source;
    }

    @Override
    public R get() {
        StreamIterator<T> it = source.iteratorChecked();
        try {
            R c = factory.get();
            while (it.hasNext()) {
                collector.accept(c, it.nextChecked());
            }
            return c;
        } finally {
            it.dispose();
        }
    }

}
