package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.davidmoten.kool.function.BiFunction;
import org.davidmoten.kool.function.BiPredicate;
import org.davidmoten.kool.function.Function;

public final class BufferWhileBuilder<T> {

    private final Stream<T> stream;

//    private Callable<? extends S> factory;
//    private BiFunction<? super S, ? super T, ? extends S> accumulator, BiPredicate<? super S, ? super T> condition,
//    boolean emitRemainder, Function<? super S, Integer> step, int maxReplay

    BufferWhileBuilder(Stream<T> stream) {
        this.stream = stream;
    }

    public <S> BuilderHasFactory<T, S> factory(Callable<? extends S> factory) {
        return new BuilderHasFactory<T, S>(stream, factory);
    }

    public BuilderHasFactoryArrayList<T> arrayList() {
        return new BuilderHasFactoryArrayList<T>(stream);
    }

    public static final class BuilderHasFactoryArrayList<T> {

        private final Stream<T> stream;

        BuilderHasFactoryArrayList(Stream<T> stream) {
            this.stream = stream;
        }

        public BuilderHasAccumulator<T, List<T>> condition(BiPredicate<? super List<T>, ? super T> condition) {
            return new BuilderHasFactory<T, List<T>>(stream, ArrayList::new) //
                    .condition(condition) //
                    .accumulator(listAccumulator());
        }
        
        private BiFunction<List<T>, T, List<T>> listAccumulator() {
            return (list, x) -> {
                list.add(x);
                return list;
            };
        }
    }

    public static final class BuilderHasFactory<T, S> {

        private final Stream<T> stream;
        private final Callable<? extends S> factory;
        private Function<? super S, Integer> step;
        private BiFunction<? super S, ? super T, ? extends S> accumulator;
        private BiPredicate<? super S, ? super T> condition;
        private boolean emitRemainder = true;
        private int maxReplay = 1024;

        BuilderHasFactory(Stream<T> stream, Callable<? extends S> factory) {
            this.stream = stream;
            this.factory = factory;
        }

        public BuilderHasCondition<T, S> condition(BiPredicate<? super S, ? super T> condition) {
            this.condition = condition;
            return new BuilderHasCondition<T, S>(this);
        }
    }

    public static final class BuilderHasCondition<T, S> {

        private final BuilderHasFactory<T, S> b;

        public BuilderHasCondition(BuilderHasFactory<T, S> b) {
            this.b = b;
        }

        public BuilderHasAccumulator<T, S> accumulator(BiFunction<? super S, ? super T, ? extends S> accumulator) {
            b.accumulator = accumulator;
            return new BuilderHasAccumulator<T, S>(b);
        }
    }

    public static final class BuilderHasAccumulator<T, S> {

        private final BuilderHasFactory<T, S> b;

        BuilderHasAccumulator(BuilderHasFactory<T, S> b) {
            this.b = b;
        }

        public BuilderHasStep<T, S> step(int step) {
            return step(x -> step);
        }

        public BuilderHasStep<T, S> step(Function<? super S, Integer> step) {
            b.step = step;
            return new BuilderHasStep<T, S>(b);
        }

    }

    public static final class BuilderHasStep<T, S> {

        private final BuilderHasFactory<T, S> b;

        BuilderHasStep(BuilderHasFactory<T, S> b) {
            this.b = b;
        }

        public BuilderHasStep<T, S> emitRemainder(boolean emitRemainder) {
            this.b.emitRemainder = emitRemainder;
            return this;
        }

        public BuilderHasStep<T, S> maxReplay(int maxReplay) {
            this.b.maxReplay = maxReplay;
            return this;
        }

        public Stream<S> build() {
            return b.stream.bufferWhile(b.factory, b.accumulator, b.condition, b.emitRemainder, b.step, b.maxReplay);
        }
    }

}
