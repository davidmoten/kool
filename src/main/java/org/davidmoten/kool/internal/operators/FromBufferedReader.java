package org.davidmoten.kool.internal.operators;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

public final class FromBufferedReader implements Stream<String> {

    private final BufferedReader reader;

    public FromBufferedReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public StreamIterator<String> iterator() {
        return new StreamIterator<String>() {
            
            String line;

            @Override
            public boolean hasNext() {
                if (line == null) {
                    try {
                        return (line = reader.readLine())!= null;
                    } catch (IOException e) {
                       throw new RuntimeException(e);
                    }
                } else {
                    return true;
                }
            }

            @Override
            public String next() {
                if (line == null) {
                    throw new NoSuchElementException();
                } else {
                    String s = line;
                    line = null;
                    return s;
                }
            }

            @Override
            public void cancel() {
                // do nothing because did not create BufferedReader
                // That is what Using operator is for
            }

        };
    }

}
