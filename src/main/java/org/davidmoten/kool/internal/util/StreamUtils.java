package org.davidmoten.kool.internal.util;

import java.util.Collections;

import org.davidmoten.kool.Stream;

public final class StreamUtils {
    
    private StreamUtils() {
        // prevent instantiation
    }
    
    public static final class EmptyHolder {
        public static final Stream<Object> EMPTY = Stream.create(Collections.emptyList());
    }
    
}
