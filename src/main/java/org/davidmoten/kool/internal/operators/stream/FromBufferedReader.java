package org.davidmoten.kool.internal.operators.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.exceptions.UncheckedException;

public final class FromBufferedReader implements Stream<String> {

    private BufferedReader reader;

    public FromBufferedReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public StreamIterator<String> iterator() {
        try {
            return new StreamIterator<String>() {
                
                String line;

                @Override
                public boolean hasNext() {
                    if (line == null) {
                        if (reader == null) {
                            return false;
                        }
                        try {
                            boolean hasNext =  (line = reader.readLine())!= null;
                            if (!hasNext) {
                                //don't close, using will do that
                                reader = null;
                            }
                            return hasNext;
                        } catch (IOException e) {
                           throw new UncheckedException(e);
                        }
                    } else {
                        return true;
                    }
                }

                @Override
                public String next() {
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
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

}
