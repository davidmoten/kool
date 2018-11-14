package org.davidmoten.kool.internal.operators.stream;

import java.util.function.Consumer;

import org.davidmoten.kool.Plugins;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class IgnoreDisposalError<T> implements Stream<T> {

    private final Stream<T> stream;
    private final Consumer<Throwable> action;

    public IgnoreDisposalError(Stream<T> stream, Consumer<Throwable> action) {
        this.stream = stream;
        this.action = action;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = Preconditions.checkNotNull(stream.iterator());

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
                try {
                    it.dispose();
                } catch (Throwable e) {
                    if (action != null) {
                        try {
                            action.accept(e);
                        } catch (Throwable e2) {
                            Plugins.onError(e2);
                        }
                    }
                }
            }

        };
    }

}
