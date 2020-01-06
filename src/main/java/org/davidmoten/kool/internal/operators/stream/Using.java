package org.davidmoten.kool.internal.operators.stream;

import java.util.concurrent.Callable;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Consumer;
import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.internal.util.Exceptions;

public final class Using<R, T> implements Stream<T> {

    private final Callable<R> resourceFactory;
    private final Function<? super R, ? extends Stream<? extends T>> streamFactory;
    private final Consumer<? super R> closer;

    public Using(Callable<R> resourceFactory, Function<? super R, ? extends Stream<? extends T>> streamFactory,
            Consumer<? super R> closer) {
        this.resourceFactory = resourceFactory;
        this.streamFactory = streamFactory;
        this.closer = closer;
    }

    @Override
    public StreamIterator<T> iterator() {
        try {
            return new StreamIterator<T>() {

                R resource = resourceFactory.call();
                
                @SuppressWarnings("unchecked")
                StreamIterator<T> it = (StreamIterator<T>) streamFactory.apply(resource).iteratorNullChecked();

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public T next() {
                    return it.nextNullChecked();
                }

                @Override
                public void dispose() {
                    it.dispose();
                    closer.acceptUnchecked(resource);
                }
            };
        } catch (Throwable e) {
            return Exceptions.rethrow(e);
        }
    }

}
