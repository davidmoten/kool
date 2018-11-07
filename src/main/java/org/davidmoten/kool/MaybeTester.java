package org.davidmoten.kool;

import java.util.Optional;

public final class MaybeTester<T> {

    private final Optional<T> value;

    public MaybeTester(Maybe<T> maybe) {
        value = maybe.get();
    }

    public void assertValue(T t) {
        if (!value.isPresent()) {
            throw new AssertionError("Value " + t + " expected but no value found");
        } else if (!value.get().equals(t)) {
            throw new AssertionError("Value " + t + " expected but found " + value.get());
        }
    }

}
