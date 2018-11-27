package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Notification;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.BaseStreamIterator;
import org.davidmoten.kool.internal.util.Exceptions;

public final class Dematerialize<T, R> implements Stream<R> {

    private final Stream<T> stream;

    public Dematerialize(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public StreamIterator<R> iterator() {
        return new BaseStreamIterator<T, R>(stream) {

            boolean empty = true;
            boolean terminated;

            @Override
            public void load() {
                // it != null && next == null
                if (it.hasNext()) {
                    empty = false;
                    @SuppressWarnings("unchecked")
                    Notification<R> v = (Notification<R>) it.next();
                    if (v.hasValue()) {
                        next = v.value();
                    } else if (v.isComplete()) {
                        dispose();
                        terminated = true;
                    } else { // error
                        Exceptions.rethrow(v.error());
                    }
                } else if (empty || !terminated) {
                    throw new NoSuchElementException(
                            "no notifications found which corresponds to a stream that emits nothing and blocks forever");
                }
            }

        };
    }

}
