package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Notification;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.BaseStreamIterator;

public class Materialize<T> implements Stream<org.davidmoten.kool.Notification<T>> {

    private final Stream<T> stream;

    public Materialize(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public StreamIterator<Notification<T>> iterator() {
        return new BaseStreamIterator<T, Notification<T>>(stream) {

            @Override
            public void load() {
                
            }

        };
    }

}
