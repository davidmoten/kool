package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Predicate;
import org.davidmoten.kool.internal.util.BaseStreamIterator;

public final class TakeWithPredicate<T> implements Stream<T> {

    private final Predicate<? super T> predicate;
    private final Stream<T> source;
    private final boolean until;

    public TakeWithPredicate(Predicate<? super T> predicate, Stream<T> source, boolean until) {
        this.predicate = predicate;
        this.source = source;
        this.until = until;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new BaseStreamIterator<T, T>(source) {

            @Override
            public void load() {
                if (next == null && it != null) {
                    if (it.hasNext()) {
                        T v = it.nextChecked();
                        boolean test = predicate.testChecked(v);
                        final boolean ok;
                        if (until) {
                            ok = !test;
                        } else {
                            ok = test;
                        }
                        if (ok) {
                            next = v;
                        } else {
                            it.dispose();
                            it = null;
                        }
                    } else {
                        it.dispose();
                        it = null;
                    }
                }
            }

        };
    }

}
