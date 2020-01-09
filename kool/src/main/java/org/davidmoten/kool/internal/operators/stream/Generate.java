package org.davidmoten.kool.internal.operators.stream;

import java.util.NoSuchElementException;

import org.davidmoten.kool.Emitter;
import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.function.Consumer;

import com.github.davidmoten.guavamini.Preconditions;

public final class Generate<T> implements Stream<T> {

    private final Consumer<? super Emitter<T>> consumer;

    public Generate(Consumer<? super Emitter<T>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new GenerateStreamIterator<T>(consumer);
    }

    private static final class GenerateStreamIterator<T> implements StreamIterator<T>, Emitter<T> {

        private final Consumer<? super Emitter<T>> consumer;
        private T next;
        private boolean complete;

        public GenerateStreamIterator(Consumer<? super Emitter<T>> consumer) {
            this.consumer = consumer;
        }

        @Override
        public boolean hasNext() {
            load();
            return next != null;
        }

        @Override
        public T next() {
            load();
            T v = next;
            if (next == null) {
                throw new NoSuchElementException();
            } else {
                next = null;
                return v;
            }
        }

        @Override
        public void onNext(T t) {
            Preconditions.checkNotNull(t);
            if (next == null) {
                next = t;
            } else {
                throw new IllegalArgumentException(
                        "emitter must only emit one event (onNext or OnComplete) per call to the consumer");
            }
        }

        @Override
        public void onComplete() {
            complete = true;
        }

        @Override
        public void dispose() {
            // do nothing

        }

        private void load() {
            if (!complete && next == null) {
                consumer.acceptUnchecked(this);
                if (!complete && next == null) {
                    throw new IllegalStateException("must call either emitter.onNext or emitter.onComplete");
                }
            }
        }

    }

}
