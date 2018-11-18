package org.davidmoten.kool;

import java.util.Iterator;

import com.github.davidmoten.guavamini.Preconditions;

public interface StreamIterator<T> extends Iterator<T> {

    /**
     * Idempotent method for cancellation.
     */
    void dispose();

    default T nextChecked() {
        return Preconditions.checkNotNull(next(), "StreamIterator cannot emit null");
    }

}
