package org.davidmoten.kool.internal.operators.stream;

import java.io.IOException;
import java.io.Reader;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.Exceptions;

public final class FromReader implements Stream<String> {

    private final Reader reader;
    private final int bufferSize;

    public FromReader(Reader reader, int bufferSize) {
        this.reader = reader;
        this.bufferSize = bufferSize;
    }

    @Override
    public StreamIterator<String> iterator() {
        return new StreamIterator<String>() {

            char[] chars = new char[bufferSize];
            int n;

            @Override
            public boolean hasNext() {
                load();
                return n != -1;
            }

            @Override
            public String next() {
                load();
                int len = n;
                n = 0;
                return new String(chars, 0, len);
            }

            @Override
            public void dispose() {
                // do nothing
            }

            private void load() {
                if (n == 0) {
                    try {
                        n = reader.read(chars);
                    } catch (IOException e) {
                        Exceptions.rethrow(e);
                    }
                }
            }

        };
    }

}
