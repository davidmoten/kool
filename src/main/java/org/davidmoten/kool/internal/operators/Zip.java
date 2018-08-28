package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.function.BiFunction;

import org.davidmoten.kool.Stream;

public class Zip<R, S, T> implements Stream<S> {

    private final Stream<T> source1;
    private final Stream<? extends R> source2;
    private final BiFunction<T, R, S> combiner;

    public Zip(Stream<T> source1, Stream<? extends R> source2, BiFunction<T, R, S> combiner) {
        this.source1 = source1;
        this.source2 = source2;
        this.combiner = combiner;
    }

    @Override
    public Iterator<S> iterator() {
        return new Iterator<S>() {

            Iterator<T> a = source1.iterator();
            Iterator<? extends R> b = source2.iterator();

            @Override
            public boolean hasNext() {
                boolean hasA = a.hasNext();
                boolean hasB = b.hasNext();
                if (hasA && hasB || !hasA && !hasB) {
                    return hasA;
                } else {
                    throw new RuntimeException("streams must have same length to be zipped");
                }
            }

            @Override
            public S next() {
                return combiner.apply(a.next(), b.next());
            }

        };
    }

}
