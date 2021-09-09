package org.davidmoten.kool.internal.operators.stream;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.BiConsumer;
import org.davidmoten.kool.function.Consumer;
import org.davidmoten.kool.internal.util.Exceptions;

public final class FlatMapGenerator<T, R> implements Stream<R> {

    private final BiConsumer<? super T, ? super Consumer<R>> generator;
    private final Stream<T> stream;
    private final Consumer<? super Consumer<R>> onFinish;

    public FlatMapGenerator(BiConsumer<? super T, ? super Consumer<R>> generator, Consumer<? super Consumer<R>> onFinish,
            Stream<T> stream) {
        this.generator = generator;
        this.onFinish = onFinish;
        this.stream = stream;
    }

    @Override
    public StreamIterator<R> iterator() {
        StreamIterator<T> it = stream.iterator();
        return new StreamIterator<R>() {

            Deque<R> queue = new ArrayDeque<>();
            Consumer<R> consumer = x -> queue.offer(x);
            boolean onFinishCalled = false;

            @Override
            public boolean hasNext() {
                load();
                return !queue.isEmpty();
            }

            @Override
            public R next() {
                load();
                if (queue.isEmpty()) {
                    throw new NoSuchElementException();
                } else {
                    return queue.poll();
                }
            }

            @Override
            public void dispose() {
                it.dispose();
            }

            private void load() {
                while (queue.isEmpty() && it.hasNext()) {
                    T t = it.next();
                    try {
                        generator.accept(t, consumer);
                    } catch (Exception e) {
                        Exceptions.rethrow(e);
                        return;
                    }
                }
                if (queue.isEmpty() && !onFinishCalled) {
                    try {
                        onFinish.accept(consumer);
                    } catch (Exception e) {
                        Exceptions.rethrow(e);
                        return;
                    }
                    onFinishCalled = true;
                }
            }

        };
    }

}
