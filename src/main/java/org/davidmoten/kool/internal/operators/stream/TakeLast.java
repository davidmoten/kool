package org.davidmoten.kool.internal.operators.stream;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.internal.util.EvictingQueue;

import com.github.davidmoten.guavamini.Preconditions;

public final class TakeLast<T> implements Stream<T> {

    private final Stream<T> stream;
    private final long n;

    public TakeLast(Stream<T> stream, long n) {
        this.stream = stream;
        this.n = n;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            StreamIterator<T> it = Preconditions.checkNotNull(stream.iterator());
            EvictingQueue<T> queue = new EvictingQueue<T>(n);
            
            @Override
            public boolean hasNext() {
                load();
                return queue.hasMoreElements();
            }

            @Override
            public T next() {
                load();
                return queue.nextElement();
            }

            @Override
            public void dispose() {
                if (it != null) {
                    it.dispose();
                    it = null;
                    queue = null;
                }
            }
            
            private void load() {
                if (it != null) {
                    while (it.hasNext()) {
                        queue.add(Preconditions.checkNotNull(it.next()));
                    }
                    it = null;
                }
            }
            
        };
    }

}
