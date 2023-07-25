package org.davidmoten.kool.internal.operators.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.BiPredicate;
import org.davidmoten.kool.function.Function;

public final class BufferWithPredicateAndStep<T> implements Stream<List<T>> {

    private final BiPredicate<? super List<T>, ? super T> condition;
    private final boolean emitRemainder;
    private final boolean until;
    private final Stream<T> source;
    private final Function<? super List<T>, Integer> step;
    private final int maxReplay;

    public BufferWithPredicateAndStep(BiPredicate<? super List<T>, ? super T> condition, boolean emitRemainder,
            boolean until, Stream<T> source, Function<? super List<T>, Integer> step, int maxReplay) {
        this.condition = condition;
        this.emitRemainder = emitRemainder;
        this.until = until;
        this.source = source;
        this.step = step;
        this.maxReplay = maxReplay;
    }

    @Override
    public StreamIterator<List<T>> iterator() {
        return new StreamIterator<List<T>>() {
            ReplayableStreamIterator<T> it = new ReplayableStreamIterator<>(source.iteratorNullChecked(), maxReplay);
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
                    int offset = step.applyUnchecked(list);
                    int bufferSize = buffer.size();
                    buffer.clear();
                    if (offset > list.size()) {
                        int n = offset - list.size();
                        // skip n values
                        for (int i = 0; i < n - bufferSize; i++) {
                            if (it.hasNext()) {
                                it.next();
                            } else {
                                break;
                            }
                        }
                    } else {
                        it.replay(list.size() - offset + bufferSize);
                    }
                    return list;
                }
            }

            @Override
            public void dispose() {
                it.dispose();
            }

            private void loadNext() {
                while (!ready && it.hasNext()) {
                    T t = it.nextNullChecked();
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
                        }
                        buffer.add(t);
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
