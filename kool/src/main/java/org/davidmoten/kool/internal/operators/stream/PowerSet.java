package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;
import java.util.Set;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Sets;

public class PowerSet implements Stream<Set<Integer>> {

    private final int n;

    public PowerSet(int n) {
        this.n = n;
    }

    @Override
    public StreamIterator<Set<Integer>> iterator() {
        return new StreamIterator<Set<Integer>>() {

            boolean[] b = new boolean[n];

            @Override
            public boolean hasNext() {
                return b != null;
            }

            @Override
            public Set<Integer> next() {
                if (b == null) {
                    throw new NoSuchElementException();
                }
                Set<Integer> set = Sets.newHashSet();
                for (int i = 0; i < b.length; i++) {
                    if (b[i]) {
                        set.add(i + 1);
                    }
                }
                int i = 0;
                while (true) {
                    if (i == b.length) {
                        break;
                    }
                    boolean x = b[i];
                    b[i] = !b[i];
                    if (!x) {
                        break;
                    }
                    i++;
                }
                if (i == b.length) {
                    b = null;
                }
                return set;
            }

            @Override
            public void dispose() {
                // do nothing
            }
        };
    }

}
