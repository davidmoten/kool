package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Predicate;
import org.davidmoten.kool.internal.util.BaseStreamIterator;
import org.davidmoten.kool.internal.util.Exceptions;

public final class Filter<T> implements Stream<T> {

    private final Predicate<? super T> predicate;
    private final StreamIterable<T> source;

    public Filter(Predicate<? super T> predicate, StreamIterable<T> source) {
        this.predicate = predicate;
        this.source = source;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new BaseStreamIterator<T, T>(source) {

            @Override
            public void load() {
                // it != null && next == null
                while (true) {
                    if (next != null) {
                        break;
                    } else if (!it.hasNext()) {
                        break;
                    } else {
                        T t = it.nextChecked();
                        try {
                            if (predicate.test(t)) {
                                next = t;
                                break;
                            }
                        } catch (Exception e) {
                            Exceptions.rethrow(e);
                            return;
                        }
                    }
                }
            }

        };
    }

}
