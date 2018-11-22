package org.davidmoten.kool.function;

import org.davidmoten.kool.internal.util.Exceptions;

@FunctionalInterface
public interface BiFunction<T, R, S> {

    S apply(T t, R r) throws Exception;

    default S applyUnchecked(T t, R r) {
        try {
            return apply(t, r);
        } catch (Exception e) {
            return Exceptions.rethrow(e);
        }
    }

}
