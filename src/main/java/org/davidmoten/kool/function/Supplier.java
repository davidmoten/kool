package org.davidmoten.kool.function;

import org.davidmoten.kool.internal.util.Exceptions;

import com.github.davidmoten.guavamini.Preconditions;

public interface Supplier<T> {

    T get() throws Exception;

    default T getNullChecked() {
        try {
            return Preconditions.checkNotNull(get());
        } catch (Exception e) {
            return Exceptions.rethrow(e);
        }
    }

}
