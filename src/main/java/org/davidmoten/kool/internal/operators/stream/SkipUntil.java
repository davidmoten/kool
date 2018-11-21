package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;
import java.util.function.Predicate;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class SkipUntil<T> implements Stream<T> {

    private final Stream<T> stream;
    private final Predicate<? super T> predicate;
    private final boolean negate;

    public SkipUntil(Stream<T> stream, Predicate<? super T> predicate, boolean negate) {
        this.stream = stream;
        this.predicate = predicate;
        this.negate = negate;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = Preconditions.checkNotNull(stream.iterator());
            T next;
            boolean nextPredicateValue;
            boolean foundFirst;

            @Override
            public boolean hasNext() {
                loadNext();
                return next != null && (foundFirst || nextPredicateValue);
            }

            private void loadNext() {
                if (it != null) {
                    if (foundFirst) {
                        if (next == null && it.hasNext()) {
                            next = it.nextChecked();
                        }
                    } else {
                        while ((next == null || !nextPredicateValue) && it.hasNext()) {
                            next = it.nextChecked();
                            nextPredicateValue = predicate.test(next);
                            if (negate)
                                nextPredicateValue = !nextPredicateValue;
                        }
                        foundFirst = next != null && nextPredicateValue;
                        if (!foundFirst) {
                            dispose();
                        }
                    }
                }
            }

            @Override
            public T next() {
                loadNext();
                if (!foundFirst) {
                    throw new NoSuchElementException();
                } else {
                    T v = next;
                    next = null;
                    return v;
                }
            }

            @Override
            public void dispose() {
                if (it != null) {
                    next = null;
                    it.dispose();
                    it = null;
                }
            }

        };
    }

}
