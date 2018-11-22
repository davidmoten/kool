package org.davidmoten.kool.function;

import org.davidmoten.kool.internal.util.Exceptions;

@FunctionalInterface
public interface Consumer<T> {

    void accept(T t) throws Exception;
    
    default void acceptUnchecked(T t) {
        try {
            accept(t);
        } catch (Exception e) {
            Exceptions.rethrow(e);
        }
    }
}
