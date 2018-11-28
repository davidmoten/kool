package org.davidmoten.kool;

import com.github.davidmoten.guavamini.Preconditions;

public interface StreamIterable<T> extends Iterable<T> {

    @Override
    StreamIterator<T> iterator();

    default StreamIterator<T> iteratorNullChecked() {
        return Preconditions.checkNotNull(iterator(), "iterator() cannot return null");
    }

}
