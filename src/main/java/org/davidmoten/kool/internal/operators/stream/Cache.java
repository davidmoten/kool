package org.davidmoten.kool.internal.operators.stream;

import java.util.List;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;

import com.github.davidmoten.guavamini.Lists;
import com.github.davidmoten.guavamini.Preconditions;

public final class Cache<T> implements Stream<T> {

    private final Stream<T> stream;
    private final List<T> list = Lists.newArrayList();
    private Stream<T> cache;

    public Cache(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public StreamIterator<T> iterator() {
        if (cache != null) {
            return cache.iterator();
        } else {
            return new StreamIterator<T>() {

                StreamIterator<T> it = Preconditions.checkNotNull(stream.iterator());

                @Override
                public boolean hasNext() {
                    if (it == null) {
                        return false;
                    }
                    boolean hasNext = it.hasNext();
                    if (!hasNext) {
                        cache = Stream.fromIterable(list);
                        dispose();
                    }
                    return hasNext;
                }

                @Override
                public T next() {
                    T v = Preconditions.checkNotNull(it.next());
                    list.add(v);
                    return v;
                }

                @Override
                public void dispose() {
                    if (it != null) {
                        it.dispose();
                        it = null;
                    }
                }
            };
        }
    }

}
