package org.davidmoten.kool.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterable;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.BiFunction;
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
    
    public static InputStream toInputStream(Stream<? extends byte[]> stream) {
        return new InputStream() {

            StreamIterator<? extends byte[]> it = stream.iteratorNullChecked();
            byte[] bytes = new byte[0];
            int index;

            @Override
            public int read() {
                load();
                if (bytes == null) {
                    return -1;
                } else {
                    return bytes[index++] & 0xFF;
                }
            }

            @Override
            public int read(byte[] b, int off, int len) {
                load();
                if (bytes == null) {
                    return -1;
                } else {
                    int length = Math.min(len, bytes.length - index);
                    System.arraycopy(bytes, index, b, off, length);
                    index += length;
                    return length;
                }
            }

            private void load() {
                if (bytes != null && index == bytes.length) {
                    while (it.hasNext()) {
                        bytes = it.nextNullChecked();
                        if (bytes.length > 0) {
                            index = 0;
                            return;
                        }
                    }
                    bytes = null;
                    it.dispose();
                }
            }

        };
    }
    
    private static final BiFunction<List<Object>, Object, List<Object>> LIST_ACCUMULATOR =  (list, t) -> {
        list.add(t);
        return list;
    };
    
    @SuppressWarnings("unchecked")
    public static <T> BiFunction<List<T>, T, List<T>> listAccumulator() {
        return (BiFunction<List<T>, T, List<T>>)(BiFunction<?, ?, ?>)LIST_ACCUMULATOR;
    }

}
