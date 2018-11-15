package org.davidmoten.kool;

import java.util.function.Function;

public final class Functions {

    private Functions() {
        // prevent instantiation
    }

    @SuppressWarnings("unchecked")
    public static <T> Function<T, T> identity() {
        return (Function<T, T>) IdentityHolder.INSTANCE;
    }
    
    private static final class IdentityHolder {
        static final Function<Object, Object> INSTANCE = x -> x;
    }
    
}
