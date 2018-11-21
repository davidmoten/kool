package org.davidmoten.kool.internal.operators.stream;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.Exceptions;

public final class Collect<T, R> implements Single<R> {

    private final Callable<? extends R> factory;
    private final BiConsumer<? super R, ? super T> collector;
    private final Stream<T> source;

    public Collect(Callable<? extends R> factory, BiConsumer<? super R, ? super T> collector, Stream<T> source) {
        this.factory = factory;
        this.collector = collector;
        this.source = source;
    }

    @Override
    public R get() {
        StreamIterator<T> it = source.iteratorChecked();
        try {
            R c = factory.call();
            while (it.hasNext()) {
                collector.accept(c, it.nextChecked());
            }
            return c;
        } catch (Exception e) {
           return Exceptions.rethrow(e);
        } finally {
            it.dispose();
        }
    }

}
