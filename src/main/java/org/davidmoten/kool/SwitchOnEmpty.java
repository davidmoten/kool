package org.davidmoten.kool;

import java.util.function.Supplier;

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
                return Preconditions.checkNotNull(it.next());
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
