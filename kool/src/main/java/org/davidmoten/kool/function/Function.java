package org.davidmoten.kool.function;

import org.davidmoten.kool.internal.util.Exceptions;
import org.davidmoten.kool.internal.util.StreamUtils;

@FunctionalInterface
public interface Function<T, R> {

    R apply(T t) throws Exception;

    default R applyUnchecked(T t) {
        try {
            return apply(t);
        } catch (Exception e) {
            return Exceptions.rethrow(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Function<T, T> identity() {
        return (Function<T, T>) StreamUtils.FunctionIdentityHolder.IDENTITY;
    }

}
