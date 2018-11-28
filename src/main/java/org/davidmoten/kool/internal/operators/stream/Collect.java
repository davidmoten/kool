package org.davidmoten.kool.internal.operators.stream;

import java.util.concurrent.Callable;

import org.davidmoten.kool.Single;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.BiFunction;
import org.davidmoten.kool.internal.util.Exceptions;

public final class Collect<T, R> implements Single<R> {

    private final Callable<? extends R> factory;
    private final BiFunction<? super R, ? super T, ? extends R> collector;
    private final Stream<T> source;

    public Collect(Callable<? extends R> factory, BiFunction<? super R, ? super T, ? extends R> collector,
            Stream<T> source) {
        this.factory = factory;
        this.collector = collector;
        this.source = source;
    }

    @Override
    public R get() {
        StreamIterator<T> it = source.iteratorNullChecked();
        try {
            R c = factory.call();
            while (it.hasNext()) {
                c = collector.apply(c, it.nextNullChecked());
            }
            return c;
        } catch (Throwable e) {
            return Exceptions.rethrow(e);
        } finally {
            it.dispose();
        }
    }

}
