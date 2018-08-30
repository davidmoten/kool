package org.davidmoten.kool.internal.operators;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Preconditions;

public final class Split implements Stream<String> {

    private final String delimiter;
    private final Stream<?> source;

    public Split(String delimiter, Stream<?> source) {
        this.delimiter = delimiter;
        this.source = source;
    }

    @Override
    public StreamIterator<String> iterator() {
        return new StreamIterator<String>() {

            StreamIterator<?> it = Preconditions.checkNotNull(source.iterator());
            StringBuilder b = new StringBuilder();
            String next;
            int startFrom;

            @Override
            public boolean hasNext() {
                loadNext();
                return next != null;
            }

            @Override
            public String next() {
                loadNext();
                if (next == null) {
                    throw new NoSuchElementException();
                } else {
                    String t = next;
                    next = null;
                    return t;
                }
            }

            @Override
            public void dispose() {
                it.dispose();
            }

            private void loadNext() {
                if (b != null && next == null) {
                    if (startFrom <= b.length() - delimiter.length()) {
                        int i = b.indexOf(delimiter, startFrom);
                        if (i != -1) {
                            next = b.substring(0, i);
                            b.delete(0, i + delimiter.length());
                            startFrom -= i + delimiter.length();
                            return;
                        }
                    }
                    while (it.hasNext()) {
                        String s = it.next().toString();
                        b.append(s);
                        int i = b.indexOf(delimiter, startFrom);
                        if (i != -1) {
                            next = b.substring(0, i);
                            b.delete(0, i + delimiter.length());
                            startFrom -= i + delimiter.length();
                            return;
                        }
                    }
                    if (b.length() > 0) {
                        next = b.toString();
                    } 
                    b = null;
                }
            }
        };
    }

}
