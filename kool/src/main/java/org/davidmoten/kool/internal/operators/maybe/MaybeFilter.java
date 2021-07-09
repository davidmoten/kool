package org.davidmoten.kool.internal.operators.maybe;

import java.util.Optional;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.function.Predicate;

public class MaybeFilter<T> implements Maybe<T> {

    private final Predicate<? super T> predicate;
    private final Maybe<T> maybe;

    public MaybeFilter(Predicate<? super T> predicate, Maybe<T> maybe) {
        this.predicate = predicate;
        this.maybe = maybe;
    }

    @Override
    public Optional<T> get() {
        Optional<T> o = maybe.get();
        if (!o.isPresent()) {
            return o;
        } else {
            if (predicate.testChecked(o.get())) {
                return o;
            } else {
                return Optional.empty();
            }
        }
    }

}
