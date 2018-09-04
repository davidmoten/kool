package org.davidmoten.kool.internal.operators.stream;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public class Using<R, T> implements Stream<T> {

    private final Supplier<R> resourceFactory;
    private final Function<? super R, ? extends Stream<? extends T>> streamFactory;
    private final Consumer<? super R> closer;

    public Using(Supplier<R> resourceFactory, Function<? super R, ? extends Stream<? extends T>> streamFactory,
            Consumer<? super R> closer) {
        this.resourceFactory = resourceFactory;
        this.streamFactory = streamFactory;
        this.closer = closer;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            R resource = resourceFactory.get();
            @SuppressWarnings("unchecked")
            StreamIterator<T> it = (StreamIterator<T>) streamFactory.apply(resource).iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }

            @Override
            public void dispose() {
                it.dispose();
                closer.accept(resource);
            }
        };
    }

}
