package org.davidmoten.kool.internal.operators.stream;

import java.util.function.Consumer;

import org.davidmoten.kool.Plugins;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class IgnoreDisposalError<T> implements Stream<T> {

    private final Stream<T> stream;
    private final Consumer<? super Throwable> action;

    public IgnoreDisposalError(Stream<T> stream, Consumer<? super Throwable> action) {
        this.stream = stream;
        this.action = action;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = stream.iteratorChecked();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.nextChecked();
            }

            @Override
            public void dispose() {
                if (it != null) {
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
                    } finally {
                        it = null;
                    }
                }
            }

        };
    }

}
