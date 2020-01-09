package org.davidmoten.kool.function;

import org.davidmoten.kool.internal.util.Exceptions;

@FunctionalInterface
public interface Predicate<T> {

    boolean test(T t) throws Exception;
    
    default boolean testChecked(T t) {
        try {
            return test(t);
        } catch (Exception e) {
            return Exceptions.rethrow(e);
        }
    }

}
