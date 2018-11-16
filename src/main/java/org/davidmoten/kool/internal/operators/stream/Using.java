package org.davidmoten.kool.internal.operators.stream;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.exceptions.UncheckedException;
import org.davidmoten.kool.internal.util.Exceptions;

import com.github.davidmoten.guavamini.Preconditions;

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
                StreamIterator<T> it = Preconditions.checkNotNull((StreamIterator<T>) streamFactory.apply(resource).iterator());

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public T next() {
                    return Preconditions.checkNotNull(it.next());
                }

                @Override
                public void dispose() {
                    it.dispose();
                    closer.accept(resource);
                }
            };
        } catch (Throwable e) {
            return Exceptions.rethrow(e);
        }
    }

}
