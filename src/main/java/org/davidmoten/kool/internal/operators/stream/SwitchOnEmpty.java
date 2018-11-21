package org.davidmoten.kool.internal.operators.stream;

import java.util.function.Supplier;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class SwitchOnEmpty<T> implements Stream<T> {

    private final Stream<T> stream;
    private Supplier<? extends Stream<T>> factory;

    public SwitchOnEmpty(Stream<T> stream, Supplier<? extends Stream<T>> factory) {
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
                return it.nextChecked();
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
                        it = Preconditions.checkNotNull(factory.get().iterator());
                    }
                }
            }

        };
    }

}
