package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Notification;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.internal.util.BaseStreamIterator;
import org.davidmoten.kool.internal.util.Exceptions;

import com.github.davidmoten.guavamini.Preconditions;

public final class Dematerialize<T, R> implements Stream<R> {

    private final Stream<T> stream;
    private final Function<? super T, Notification<? extends R>> function;

    public Dematerialize(Stream<T> stream, Function<? super T, Notification<? extends R>> function) {
        Preconditions.checkNotNull(function);
        this.stream = stream;
        this.function = function;
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
                    Notification<? extends R> v = Preconditions.checkNotNull(function.applyUnchecked(it.next()), "function cannot return null");
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
