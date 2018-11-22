package org.davidmoten.kool.internal.operators.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.BiPredicate;

public final class BufferWithPredicate<T> implements Stream<List<T>> {

    private final BiPredicate<? super List<T>, ? super T> condition;
    private final boolean emitRemainder;
    private final boolean until;
    private final Stream<T> source;

    public BufferWithPredicate(BiPredicate<? super List<T>, ? super T> condition,
            boolean emitRemainder, boolean until, Stream<T> source) {
        this.condition = condition;
        this.emitRemainder = emitRemainder;
        this.until = until;
        this.source = source;
    }

    @Override
    public StreamIterator<List<T>> iterator() {
        return new StreamIterator<List<T>>() {
            StreamIterator<T> it = source.iteratorChecked();
            List<T> buffer = new ArrayList<>();
            List<T> nextBuffer = new ArrayList<>();
            boolean ready;

            @Override
            public boolean hasNext() {
                loadNext();
                return ready;
            }

            @Override
            public List<T> next() {
                loadNext();
                if (!ready) {
                    throw new NoSuchElementException();
                } else {
                    List<T> list = buffer;
                    buffer = nextBuffer;
                    nextBuffer = new ArrayList<>();
                    ready = false;
                    return list;
                }
            }

            @Override
            public void dispose() {
                it.dispose();
            }

            private void loadNext() {
                while (!ready && it.hasNext()) {
                    T t = it.nextChecked();
                    boolean b = condition.testUnchecked(buffer, t);
                    if (!until) {
                        // while
                        if (b) {
                            buffer.add(t);
                        } else {
                            ready = true;
                            nextBuffer.add(t);
                        }
                    } else {
                        // until
                        if (b) {
                            ready = true;
                            nextBuffer.add(t);
                        } else {
                            buffer.add(t);
                        }
                    }
                }
                if (!ready) {
                    if (emitRemainder && !buffer.isEmpty()) {
                        ready = true;
                    }
                } else {
                    ready = true;
                }
            }
        };
    }

}
