package org.davidmoten.kool.function;

import org.davidmoten.kool.internal.util.Exceptions;

@FunctionalInterface
public interface BiPredicate<T, R> {

    boolean test(T t, R r) throws Exception;

    default boolean testUnchecked(T t, R r) {
        try {
            return test(t, r);
        } catch (Exception e) {
            return Exceptions.rethrow(e);
        }
    }
    
}
