package org.davidmoten.kool;

import java.util.Iterator;

public interface StreamIterator<T> extends Iterator<T> {

    /**
     * Idempotent method for cancellation.
     */
    void cancel();
}
