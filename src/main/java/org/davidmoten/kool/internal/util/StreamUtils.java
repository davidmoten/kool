package org.davidmoten.kool.internal.util;

import java.util.Collections;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Function;

import com.github.davidmoten.guavamini.Preconditions;

public final class StreamUtils {

    private StreamUtils() {
        // prevent instantiation
    }

    public static final class EmptyHolder {
        public static final Stream<Object> EMPTY = Stream.create(Collections.emptyList());
    }

    public static final class FunctionIdentityHolder {
        public static final Function<Object, Object> IDENTITY = x -> x;
    }

    public static <T> StreamIterator<T> iterator(StreamIterable<T> iter) {
        return Preconditions.checkNotNull(iter.iterator(), "iterator cannot be null");
    }

    public static <T> T next(StreamIterator<T> it) {
        return Preconditions.checkNotNull(it.next(), "upstream cannot emit null");
    }

}
