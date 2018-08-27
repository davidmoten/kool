package org.davidmoten.kool;

import java.util.function.Predicate;

public final class Predicates {

    private Predicates() {
        // prevent instantiation
    }

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysTrue() {
        return (Predicate<T>) AlwaysTrueHolder.INSTANCE;
    }

    private static final class AlwaysTrueHolder {
        static final Predicate<Object> INSTANCE = t -> true;
    }

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysFalse() {
        return (Predicate<T>) AlwaysFalseHolder.INSTANCE;
    }

    private static final class AlwaysFalseHolder {
        static final Predicate<Object> INSTANCE = t -> false;
    }

}
