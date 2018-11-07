package org.davidmoten.kool.internal.operators.maybe;

import org.davidmoten.kool.Maybe;
import org.davidmoten.kool.Single;

public final class MaybeIsPresent implements Single<Boolean> {

    private final Maybe<?> maybe;

    public MaybeIsPresent(Maybe<?> maybe) {
        this.maybe = maybe;
    }

    @Override
    public Boolean get() {
        return maybe.get().isPresent();
    }

}
