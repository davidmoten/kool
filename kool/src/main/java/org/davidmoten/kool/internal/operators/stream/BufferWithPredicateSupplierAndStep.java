package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.BiFunction;
import org.davidmoten.kool.function.BiPredicate;
import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.function.Supplier;

public final class BufferWithPredicateSupplierAndStep<S, T> implements Stream<S> {

    private final Supplier<? extends S> factory;
    private final BiFunction<? super S, ? super T, ? extends S> accumulator;
    private final BiPredicate<? super S, ? super T> condition;
    private final boolean emitRemainder;
    private final boolean until;
    private final Stream<T> source;
    private final Function<? super S, Integer> step;
    private final int maxReplay;

    public BufferWithPredicateSupplierAndStep(Supplier<? extends S> factory,
            BiFunction<? super S, ? super T, ? extends S> accumulator, BiPredicate<? super S, ? super T> condition,
            boolean emitRemainder, boolean until, Stream<T> source, Function<? super S, Integer> step, int maxReplay) {
        this.factory = factory;
        this.accumulator = accumulator;
        this.condition = condition;
        this.emitRemainder = emitRemainder;
        this.until = until;
        this.source = source;
        this.step = step;
        this.maxReplay = maxReplay;
    }

    @Override
    public StreamIterator<S> iterator() {
        return new StreamIterator<S>() {
            ReplayableStreamIterator<T> it = new ReplayableStreamIterator<>(source.iteratorNullChecked(), maxReplay);
            Buffer<S, T> buffer = new Buffer<>(accumulator, factory.get());
            Buffer<S, T> nextBuffer = new Buffer<>(accumulator, factory.get());
            boolean ready;

            @Override
            public boolean hasNext() {
                loadNext();
                return ready;
            }

            @Override
            public S next() {
                loadNext();
                if (!ready) {
                    throw new NoSuchElementException();
                } else {
                    Buffer<S, T> current = buffer;
                    buffer = nextBuffer;
                    nextBuffer = nextBuffer.create(factory.get());
                    ready = false;
                    int offset = step.applyUnchecked(current.state);
                    if (offset > current.count) {
                        int n = offset - current.count;
                        // skip n values
                        for (int i = 0; i < n - buffer.count; i++) {
                            if (it.hasNext()) {
                                it.next();
                            } else {
                                break;
                            }
                        }
                    } else {
                        it.replay(current.count - offset + buffer.count);
                    }
                    return current.state;
                }
            }

            @Override
            public void dispose() {
                it.dispose();
            }

            private void loadNext() {
                try {
                    while (!ready && it.hasNext()) {
                        T t = it.nextNullChecked();
                        boolean b = condition.testUnchecked(buffer.state, t);
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
                } catch (Exception e) {
                    throw new RuntimeException(e);
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

    private static final class Buffer<S, T> {

        private final BiFunction<? super S, ? super T, ? extends S> accumulator;
        S state;
        int count;

        Buffer(BiFunction<? super S, ? super T, ? extends S> accumulator, S state) {
            this.accumulator = accumulator;
            this.state = state;
        }

        boolean isEmpty() {
            return count == 0;
        }

        void add(T t) throws Exception {
            count++;
            state = accumulator.apply(state, t);
        }

        Buffer<S, T> create(S s) {
            return new Buffer<>(accumulator, s);
        }
    }

}
