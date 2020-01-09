package org.davidmoten.kool.function;

import org.davidmoten.kool.internal.util.Exceptions;

@FunctionalInterface
public interface Action {

    void call() throws Exception;
    
    default void callUnchecked() {
        try {
            call();
        } catch (Exception e) {
            Exceptions.rethrow(e);
        }
    }

}
