package org.davidmoten.kool.internal.operators.maybe;

import java.util.Optional;

import org.davidmoten.kool.Maybe;

public class MaybeDoOnEmpty<T> implements Maybe<T> {

    private final Maybe<T> maybe;
    private final Runnable action;

    public MaybeDoOnEmpty(Maybe<T> maybe, Runnable action) {
        this.maybe = maybe;
        this.action = action;
    }

    @Override
    public Optional<T> get() {
        Optional<T> v = maybe.get();
        if (!v.isPresent()) {
            action.run();
        }
        return v;
    }

}
