package org.davidmoten.kool.internal.operators.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class FromInputStream implements Stream<ByteBuffer> {

    private InputStream is;
    private final int bufferSize;

    public FromInputStream(InputStream is, int bufferSize) {
        Preconditions.checkNotNull(is);
        Preconditions.checkArgument(bufferSize > 0);
        this.is = is;
        this.bufferSize = bufferSize;
    }

    @Override
    public StreamIterator<ByteBuffer> iterator() {
        return new StreamIterator<ByteBuffer>() {

            ByteBuffer next;

            @Override
            public boolean hasNext() {
                load();
                return next != null;
            }

            @Override
            public ByteBuffer next() {
                load();
                ByteBuffer v = next;
                if (v == null) {
                    throw new NoSuchElementException();
                }
                next = null;
                return v;
            }

            @Override
            public void dispose() {
                if (is != null) {
                    is = null;
                    next = null;
                }
            }

            private void load() {
                if (is != null && next == null) {
                    byte[] b = new byte[bufferSize];
                    int n;
                    try {
                        n = is.read(b);
                    } catch (IOException e) {
                        is = null;
                        throw new UncheckedIOException(e);
                    }
                    if (n == -1) {
                        is = null;
                    } else {
                        next = ByteBuffer.wrap(b, 0, n);
                    }
                }
            }

        };
    }

}
