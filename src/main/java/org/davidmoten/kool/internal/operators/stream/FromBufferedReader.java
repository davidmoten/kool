package org.davidmoten.kool.internal.operators.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class FromBufferedReader implements Stream<String> {

    private BufferedReader reader;

    public FromBufferedReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public StreamIterator<String> iterator() {
        return new StreamIterator<String>() {

            String line;

            @Override
            public boolean hasNext() {
                load();
                return line != null;
            }

            private void load() {
                if (line == null && reader!= null) {
                    try {
                        boolean hasNext = (line = reader.readLine()) != null;
                        if (!hasNext) {
                            // don't close, using will do that
                            reader = null;
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }              
            }

            @Override
            public String next() {
                load();
                if (line == null) {
                    dispose();
                    throw new NoSuchElementException();
                } else {
                    String s = line;
                    line = null;
                    return s;
                }
            }

            @Override
            public void dispose() {
                // don't close reader, using operator would do that
                reader = null;
                line = null;
            }

        };
    }

}
