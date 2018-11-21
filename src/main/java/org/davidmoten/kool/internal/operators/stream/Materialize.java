package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Notification;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.BaseStreamIterator;

public final class Materialize<T> implements Stream<org.davidmoten.kool.Notification<T>> {

    private final Stream<T> stream;

    public Materialize(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public StreamIterator<Notification<T>> iterator() {
        return new BaseStreamIterator<T, Notification<T>>(stream) {

            boolean emittedTerminal;

            @Override
            public StreamIterator<T> init(StreamIterable<T> stream) {
                try {
                    return stream.iteratorChecked();
                } catch (Throwable e) {
                    emittedTerminal = true;
                    dispose();
                    next = Notification.error(e);
                    return null;
                }
            }

            @Override
            public void load() {
                // it != null && next == null
                if (!emittedTerminal) {
                    try {
                        if (it.hasNext()) {
                            next = Notification.of(it.nextChecked());
                        } else {
                            emittedTerminal = true;
                            dispose();
                            next = Notification.complete();
                        }
                    } catch (Throwable e) {
                        emittedTerminal = true;
                        dispose();
                        next = Notification.error(e);
                    }
                }
            }

        };
    }

}
