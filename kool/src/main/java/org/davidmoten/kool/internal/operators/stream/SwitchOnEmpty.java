package org.davidmoten.kool.internal.operators.stream;

import java.util.concurrent.Callable;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.Exceptions;

import com.github.davidmoten.guavamini.Preconditions;

public final class SwitchOnEmpty<T> implements Stream<T> {

    private final Stream<T> stream;
    private Callable<? extends Stream<T>> factory;

    public SwitchOnEmpty(Stream<T> stream, Callable<? extends Stream<T>> factory) {
        this.stream = stream;
        this.factory = factory;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = stream.iterator();

            boolean checkedForEmpty;

            @Override
            public boolean hasNext() {
                check();
                return it.hasNext();
            }

            @Override
            public T next() {
                check();
                return it.nextNullChecked();
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    it = null;
                }
            }
            
            private void check() {
                if (!checkedForEmpty) {
                    checkedForEmpty = true;
                    if (!it.hasNext()) {
                        it.dispose();
                        try {
                            it = Preconditions.checkNotNull(factory.call().iterator());
                        } catch (Exception e) {
                            Exceptions.rethrow(e);
                        }
                    }
                }
            }

        };
    }

}
