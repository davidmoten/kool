package org.davidmoten.kool;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.davidmoten.kool.function.Action;
import org.davidmoten.kool.function.BiConsumer;
import org.davidmoten.kool.function.BiFunction;
import org.davidmoten.kool.function.BiPredicate;
import org.davidmoten.kool.function.Consumer;
import org.davidmoten.kool.function.Consumers;
import org.davidmoten.kool.function.Function;
import org.davidmoten.kool.function.Predicate;
import org.davidmoten.kool.function.Predicates;
import org.davidmoten.kool.internal.operators.stream.All;
import org.davidmoten.kool.internal.operators.stream.Any;
import org.davidmoten.kool.internal.operators.stream.Buffer;
import org.davidmoten.kool.internal.operators.stream.BufferWithFactoryPredicateAndStep;
import org.davidmoten.kool.internal.operators.stream.BufferWithPredicate;
import org.davidmoten.kool.internal.operators.stream.Cache;
import org.davidmoten.kool.internal.operators.stream.Collect;
import org.davidmoten.kool.internal.operators.stream.Concat;
import org.davidmoten.kool.internal.operators.stream.Count;
import org.davidmoten.kool.internal.operators.stream.Defer;
import org.davidmoten.kool.internal.operators.stream.Dematerialize;
import org.davidmoten.kool.internal.operators.stream.Distinct;
import org.davidmoten.kool.internal.operators.stream.DistinctUntilChanged;
import org.davidmoten.kool.internal.operators.stream.DoOnComplete;
import org.davidmoten.kool.internal.operators.stream.DoOnDispose;
import org.davidmoten.kool.internal.operators.stream.DoOnEmpty;
import org.davidmoten.kool.internal.operators.stream.DoOnError;
import org.davidmoten.kool.internal.operators.stream.DoOnNext;
import org.davidmoten.kool.internal.operators.stream.DoOnStart;
import org.davidmoten.kool.internal.operators.stream.Filter;
import org.davidmoten.kool.internal.operators.stream.First;
import org.davidmoten.kool.internal.operators.stream.FlatMap;
import org.davidmoten.kool.internal.operators.stream.FlatMapGenerator;
import org.davidmoten.kool.internal.operators.stream.FromArray;
import org.davidmoten.kool.internal.operators.stream.FromArrayDouble;
import org.davidmoten.kool.internal.operators.stream.FromArrayFloat;
import org.davidmoten.kool.internal.operators.stream.FromArrayInt;
import org.davidmoten.kool.internal.operators.stream.FromBufferedReader;
import org.davidmoten.kool.internal.operators.stream.FromChars;
import org.davidmoten.kool.internal.operators.stream.FromInputStream;
import org.davidmoten.kool.internal.operators.stream.FromReader;
import org.davidmoten.kool.internal.operators.stream.Generate;
import org.davidmoten.kool.internal.operators.stream.IgnoreDisposalError;
import org.davidmoten.kool.internal.operators.stream.IsEmpty;
import org.davidmoten.kool.internal.operators.stream.Last;
import org.davidmoten.kool.internal.operators.stream.Map;
import org.davidmoten.kool.internal.operators.stream.Materialize;
import org.davidmoten.kool.internal.operators.stream.Max;
import org.davidmoten.kool.internal.operators.stream.MergeInterleaved;
import org.davidmoten.kool.internal.operators.stream.PowerSet;
import org.davidmoten.kool.internal.operators.stream.PrependOne;
import org.davidmoten.kool.internal.operators.stream.Range;
import org.davidmoten.kool.internal.operators.stream.RangeLong;
import org.davidmoten.kool.internal.operators.stream.ReduceNoInitialValue;
import org.davidmoten.kool.internal.operators.stream.ReduceWithInitialValueSupplier;
import org.davidmoten.kool.internal.operators.stream.Repeat;
import org.davidmoten.kool.internal.operators.stream.RepeatElement;
import org.davidmoten.kool.internal.operators.stream.RepeatLast;
import org.davidmoten.kool.internal.operators.stream.RetryWhen;
import org.davidmoten.kool.internal.operators.stream.Reverse;
import org.davidmoten.kool.internal.operators.stream.Skip;
import org.davidmoten.kool.internal.operators.stream.SkipLast;
import org.davidmoten.kool.internal.operators.stream.SkipUntil;
import org.davidmoten.kool.internal.operators.stream.Sorted;
import org.davidmoten.kool.internal.operators.stream.Split;
import org.davidmoten.kool.internal.operators.stream.SwitchOnEmpty;
import org.davidmoten.kool.internal.operators.stream.SwitchOnError;
import org.davidmoten.kool.internal.operators.stream.Take;
import org.davidmoten.kool.internal.operators.stream.TakeLast;
import org.davidmoten.kool.internal.operators.stream.TakeWithPredicate;
import org.davidmoten.kool.internal.operators.stream.ToMaybe;
import org.davidmoten.kool.internal.operators.stream.ToSingle;
import org.davidmoten.kool.internal.operators.stream.Transform;
import org.davidmoten.kool.internal.operators.stream.TransformMaybe;
import org.davidmoten.kool.internal.operators.stream.TransformSingle;
import org.davidmoten.kool.internal.operators.stream.Using;
import org.davidmoten.kool.internal.operators.stream.Zip;
import org.davidmoten.kool.internal.util.Exceptions;
import org.davidmoten.kool.internal.util.Iterables;
import org.davidmoten.kool.internal.util.Permutations;
import org.davidmoten.kool.internal.util.StreamImpl;
import org.davidmoten.kool.internal.util.StreamUtils;

import com.github.davidmoten.guavamini.Preconditions;

public interface Stream<T> extends StreamIterable<T> {

    int DEFAULT_BUFFER_SIZE = 16;
    int DEFAULT_BYTE_BUFFER_SIZE = 8192;

    //////////////////
    // Factories
    //////////////////

    static <T> Stream<T> create(Iterable<? extends T> source) {
        return new StreamImpl<T>(source);
    }

    static <T> Stream<T> of(T t) {
        return create(Collections.singleton(t));
    }

    static <T> Stream<T> of(T t1, T t2) {
        return create(Iterables.ofNoCopy(t1, t2));
    }

    static <T> Stream<T> of(T t1, T t2, T t3) {
        return create(Iterables.ofNoCopy(t1, t2, t3));
    }

    static <T> Stream<T> of(T t1, T t2, T t3, T t4) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4));
    }

    static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5));
    }

    static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5, t6));
    }

    static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5, t6, t7));
    }

    static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5, t6, t7, t8));
    }

    static <T> Stream<T> error(Throwable e) {
        return Stream.from(new StreamIterable<T>() {
            @Override
            public StreamIterator<T> iterator() {
                return Exceptions.rethrow(e);
            }
        });
    }

    static Stream<Integer> chars(CharSequence s) {
        return chars(s, 0, s.length());
    }

    static Stream<Integer> chars(CharSequence s, int fromIndex, int toIndex) {
        return new FromChars(s, fromIndex, toIndex);
    }

    static <T> Stream<T> generate(Consumer<Emitter<T>> consumer) {
        return new Generate<T>(consumer);
    }

    static <T, R> Stream<T> generate(Callable<R> factory, BiConsumer<R, Emitter<T>> consumer) {
        return Stream.defer(() -> {
            R r = factory.call();
            return generate(e -> {
                consumer.accept(r, e);
            });
        });
    }

    static <T> Stream<T> error(Callable<? extends Throwable> callable) {
        return Stream.from(new StreamIterable<T>() {
            @Override
            public StreamIterator<T> iterator() {
                return Exceptions.rethrow(callable);
            }
        });
    }

    static <T> Stream<T> from(Iterable<? extends T> iterable) {
        return create(iterable);
    }

    static Stream<String> from(Reader reader) {
        return new FromReader(reader, DEFAULT_BUFFER_SIZE);
    }

    static Stream<String> from(Reader reader, int bufferSize) {
        return new FromReader(reader, bufferSize);
    }

    static Stream<String> from(InputStream in, Charset charset, int bufferSize) {
        return new FromReader(new InputStreamReader(in, charset), bufferSize);
    }

    static Stream<String> from(InputStream in, Charset charset) {
        return from(in, charset, DEFAULT_BUFFER_SIZE);
    }

    static Stream<String> from(InputStream in) {
        return from(in, StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    static <T> Stream<T> from(java.util.stream.Stream<? extends T> stream) {
        return from(() -> (Iterator<T>) stream.iterator()).doOnDispose(() -> stream.close());
    }

    static <T> Stream<T> fromArray(T[] array, int fromIndex, int toIndex) {
        return new FromArray<T>(array, fromIndex, toIndex);
    }

    static <T> Stream<T> fromArray(T[] array) {
        if (array.length == 0) {
            return Stream.empty();
        } else {
            return fromArray(array, 0, array.length);
        }
    }

    static Stream<Integer> fromArray(int[] array, int fromIndex, int toIndex) {
        return new FromArrayInt(array, fromIndex, toIndex);
    }

    static Stream<Integer> fromArray(int[] array) {
        if (array.length == 0) {
            return Stream.empty();
        } else {
            return fromArray(array, 0, array.length);
        }
    }

    static Stream<Double> fromArray(double[] array, int fromIndex, int toIndex) {
        return new FromArrayDouble(array, fromIndex, toIndex);
    }

    static Stream<Double> fromArray(double[] array) {
        if (array.length == 0) {
            return Stream.empty();
        } else {
            return fromArray(array, 0, array.length);
        }
    }

    static Stream<Float> fromArray(float[] array, int fromIndex, int toIndex) {
        return new FromArrayFloat(array, fromIndex, toIndex);
    }

    static Stream<Float> fromArray(float[] array) {
        if (array.length == 0) {
            return Stream.empty();
        } else {
            return fromArray(array, 0, array.length);
        }
    }

    static Stream<String> lines(BufferedReader reader) {
        return new FromBufferedReader(reader);
    }

    static Stream<String> lines(Callable<? extends BufferedReader> readerFactory) {
        return Stream.using(() -> {
            try {
                return readerFactory.call();
            } catch (Exception e) {
                return Exceptions.rethrow(e);
            }
        }, br -> lines(br));
    }

    static Stream<String> lines(Callable<? extends InputStream> inFactory, Charset charset) {
        return lines(() -> new BufferedReader(new InputStreamReader(inFactory.call(), charset)));
    }

    static Stream<String> lines(File file, Charset charset) {
        return Stream.using(() -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        }, in -> lines(() -> in, charset));
    }

    static Stream<String> lines(File file) {
        return lines(file, StandardCharsets.UTF_8);
    }

    static Stream<String> linesFromResource(String resource, Charset charset) {
        return linesFromResource(Stream.class, resource, charset);
    }

    static Stream<String> linesFromResource(Class<?> cls, String resource, Charset charset) {
        return lines(() -> cls.getResourceAsStream(resource), charset);
    }

    static Stream<String> linesFromResource(String resource) {
        return linesFromResource(Stream.class, resource, StandardCharsets.UTF_8);
    }

    static Stream<ByteBuffer> byteBuffers(Callable<? extends InputStream> provider, int bufferSize) {
        return using(provider, is -> byteBuffers(is));
    }

    static Stream<ByteBuffer> byteBuffers(Callable<? extends InputStream> provider) {
        return byteBuffers(provider, DEFAULT_BYTE_BUFFER_SIZE);
    }

    static Stream<ByteBuffer> byteBuffers(InputStream in) {
        return byteBuffers(in, DEFAULT_BYTE_BUFFER_SIZE);
    }

    static Stream<ByteBuffer> byteBuffers(InputStream in, int bufferSize) {
        return new FromInputStream(in, bufferSize);
    }

    static Stream<byte[]> bytes(Callable<? extends InputStream> provider, int bufferSize) {
        return byteBuffers(provider, bufferSize) //
                .map(bb -> {
                    byte[] b = new byte[bb.remaining()];
                    bb.get(b);
                    return b;
                });
    }

    static Stream<byte[]> bytes(Callable<? extends InputStream> provider) {
        return bytes(provider, DEFAULT_BYTE_BUFFER_SIZE);
    }

    static Stream<byte[]> bytes(InputStream in, int bufferSize) {
        return byteBuffers(in, bufferSize)//
                .map(bb -> {
                    byte[] b = new byte[bb.remaining()];
                    bb.get(b);
                    return b;
                });
    }

    static Stream<byte[]> bytes(InputStream in) {
        return bytes(in, DEFAULT_BUFFER_SIZE);
    }

    static Stream<Integer> range(int start, int length) {
        return new Range(start, length);
    }

    static Stream<Long> rangeLong(long start, long length) {
        return new RangeLong(start, length);
    }

    static Stream<Long> ordinalsLong() {
        return rangeLong(1, Long.MAX_VALUE);
    }

    static Stream<Integer> ordinals() {
        return range(1, Integer.MAX_VALUE);
    }

    static <T> Stream<T> defer(Callable<? extends Stream<? extends T>> provider) {
        return new Defer<T>(provider);
    }

    @SuppressWarnings("unchecked")
    static <T> Stream<T> empty() {
        return (Stream<T>) StreamUtils.EmptyHolder.EMPTY;
    }

    static <R, T> Stream<T> using(Callable<? extends R> resourceFactory,
            Function<? super R, ? extends Stream<? extends T>> streamFactory, Consumer<? super R> closer) {
        return new Using<R, T>(resourceFactory, streamFactory, closer);
    }

    static <R extends Closeable, T> Stream<T> using(Callable<? extends R> resourceFactory,
            Function<? super R, ? extends Stream<? extends T>> streamFactory) {
        return new Using<R, T>(resourceFactory, streamFactory, CLOSEABLE_CLOSER);
    }

    Consumer<Closeable> CLOSEABLE_CLOSER = new Consumer<Closeable>() {

        @Override
        public void accept(Closeable c) {
            try {
                c.close();
            } catch (IOException e) {
                Plugins.onError(e);
            }
        }

    };

    static <T> Stream<T> repeatElement(T t) {
        return repeatElement(t, Long.MAX_VALUE);
    }

    static <T> Stream<T> repeatElement(T t, long count) {
        return new RepeatElement<T>(t, count);
    }

    /**
     * Returns an interleaved merge of the streams (one item emitted from each
     * stream in round-robin style).
     * 
     * @param streams to be merged
     * @param <T>     result stream type
     * @return merges streams (interleaved)
     */
    @SafeVarargs
    static <T> Stream<T> merge(Stream<? extends T>... streams) {
        return new MergeInterleaved<T>(streams);
    }

    /**
     * Emits the integers 0, 1, 2, .... The first element is emitted immediately (0)
     * and then the current thread is blocked with {@code Thread.sleep} for the
     * given duration between further emissions.
     * 
     * If you don't want the stream to start with 0 immediately then call
     * {@code interval(...).skip(1)}.
     * 
     * @param duration sleep duration
     * @param unit     unit of sleep duration
     * @return stream with interval wait between emissions
     */
    static Stream<Integer> interval(long duration, TimeUnit unit) {
        return range(1, Integer.MAX_VALUE).doOnNext(x -> unit.sleep(duration)).prepend(0);
    }
    
    static InputStream inputStream(Stream<? extends byte[]> stream) {
        return StreamUtils.toInputStream(stream);
    }

    static Stream<String> strings(Stream<? extends byte[]> stream, Charset charset, int bufferSize) {
        return defer(() -> Stream.from(new InputStreamReader(inputStream(stream), charset.newDecoder()), bufferSize));
    }

    static Stream<String> strings(Stream<? extends byte[]> stream, Charset charset) {
        return strings(stream, charset, DEFAULT_BUFFER_SIZE);
    }

    static Stream<String> strings(Stream<? extends byte[]> stream) {
        return strings(stream, StandardCharsets.UTF_8);
    }

    //////////////////
    // Operators
    //////////////////

    default Single<Boolean> isEmpty() {
        return new IsEmpty(this);
    }

    default Single<Boolean> hasElements() {
        return isEmpty().map(x -> !x);
    }

    default Stream<T> sorted(Comparator<? super T> comparator) {
        return new Sorted<T>(comparator, this);
    }

    @SuppressWarnings("unchecked")
    default Stream<T> sorted() {
        return (Stream<T>) ((Stream<Comparable<Object>>) this).sorted(Comparator.naturalOrder());
    }

    default <R> Stream<R> map(Function<? super T, ? extends R> function) {
        return new Map<T, R>(function, this);
    }

    default Maybe<T> reduce(BiFunction<? super T, ? super T, ? extends T> reducer) {
        return new ReduceNoInitialValue<T>(reducer, this);
    }

    default <R> Single<R> reduce(R initialValue, BiFunction<? super R, ? super T, ? extends R> reducer) {
        return reduceWithFactory(() -> initialValue, reducer);
    }

    default <R> Single<R> reduceWithFactory(Callable<? extends R> initialValueFactory,
            BiFunction<? super R, ? super T, ? extends R> reducer) {
        return new ReduceWithInitialValueSupplier<R, T>(initialValueFactory, reducer, this);
    }

    default <R> Single<Integer> sumInt(Function<? super T, Integer> mapper) {
        return reduce(0, (x, y) -> x + mapper.apply(y));
    }

    default <R> Single<Long> sumLong(Function<? super T, Long> mapper) {
        return reduce(0L, (x, y) -> x + mapper.apply(y));
    }

    default <R> Single<Double> sumDouble(Function<? super T, Double> mapper) {
        return reduce(0.0, (x, y) -> x + mapper.apply(y));
    }

    default <R> Single<R> collect(Callable<? extends R> factory, BiConsumer<? super R, ? super T> collector) {
        return new Collect<T, R>(factory, collector, this);
    }

    default <R> Single<R> collect(Collector<T, R> collector) {
        return reduceWithFactory(collector, collector);
    }

    default <A, R> Single<R> collect(java.util.stream.Collector<T, A, R> collector) {

        Supplier<A> supplier = collector.supplier();
        java.util.function.BiConsumer<A, T> accumulator = collector.accumulator();
        Function<A, R> finisher = new Function<A, R>() {
            final java.util.function.Function<A, R> finisher = collector.finisher();

            @Override
            public R apply(A a) throws Exception {
                return finisher.apply(a);
            }
        };
        return collect(new Collector<T, A>() {

            @Override
            public A call() throws Exception {
                return supplier.get();
            }

            @Override
            public A apply(A a, T t) throws Exception {
                accumulator.accept(a, t);
                return a;
            }

        }).map(finisher);
    }

    default <M extends java.util.Map<K, D>, K, V, D extends Collection<V>> Single<M> groupBy( //
            Callable<M> mapFactory, //
            Function<? super T, ? extends K> keySelector, //
            Function<? super T, ? extends V> valueSelector, //
            Callable<D> collectionFactory) {
        return collect(mapFactory, (map, t) -> {
            K k = keySelector.apply(t);
            V v = valueSelector.apply(t);
            D x = map.get(k);
            if (x == null) {
                try {
                    x = collectionFactory.call();
                    map.put(k, x);
                } catch (Exception e) {
                    Exceptions.rethrow(e);
                }
            }
            x.add(v);
        });
    }

    default <M extends java.util.Map<K, List<V>>, K, V> Single<M> groupByList( //
            Callable<M> mapFactory, //
            Function<? super T, ? extends K> keySelector, //
            Function<? super T, ? extends V> valueSelector) {
        return groupBy(mapFactory, keySelector, valueSelector, ArrayList::new);
    }

    default <M extends java.util.Map<K, Set<V>>, K, V> Single<M> groupBySet( //
            Callable<M> mapFactory, //
            Function<? super T, ? extends K> keySelector, //
            Function<? super T, ? extends V> valueSelector) {
        return groupBy(mapFactory, keySelector, valueSelector, HashSet::new);
    }

    default <M extends java.util.Map<K, List<T>>, K> Single<M> groupByList( //
            Callable<M> mapFactory, //
            Function<? super T, ? extends K> keySelector) {
        return groupByList(mapFactory, keySelector, Function.identity());
    }

    default <M extends java.util.Map<K, Set<T>>, K> Single<M> groupBySet( //
            Callable<M> mapFactory, //
            Function<? super T, ? extends K> keySelector) {
        return groupBySet(mapFactory, keySelector, Function.identity());
    }

    default <K> Single<java.util.Map<K, List<T>>> groupByList( //
            Function<? super T, ? extends K> keySelector) {
        return groupByList(HashMap::new, keySelector);
    }

    default <K> Single<java.util.Map<K, Set<T>>> groupBySet( //
            Function<? super T, ? extends K> keySelector) {
        return groupBySet(HashMap::new, keySelector);
    }

    default Single<T> single() {
        return new ToSingle<T>(this);
    }

    default Single<List<T>> toList() {
        return toList(DEFAULT_BUFFER_SIZE);
    }

    default Single<Set<T>> toSet() {
        return toSet(DEFAULT_BUFFER_SIZE);
    }

    default Single<Set<T>> toSet(int sizeHint) {
        return collect(() -> new HashSet<T>(sizeHint), (BiConsumer<Set<T>, T>) (set, x) -> set.add(x));
    }

    default Single<List<T>> toList(int sizeHint) {
        return collect(() -> new ArrayList<T>(sizeHint), (BiConsumer<List<T>, T>) (list, x) -> list.add(x));
    }

    default Stream<T> filter(Predicate<? super T> function) {
        if (function == Predicates.alwaysTrue()) {
            return this;
        } else {
            return new Filter<T>(function, this);
        }
    }

    default Single<Boolean> exists(Predicate<? super T> function) {
        return filter(function).isEmpty().map(x -> !x);
    }
    
    default Single<Boolean> noneMatch(Predicate<? super T> function) {
        return filter(function).isEmpty();
    }

    default void forEach() {
        count().get();
    }

    default void go() {
        forEach();
    }

    default void start() {
        forEach();
    }

    default void forEach2(Consumer<? super T> consumer) {
        doOnNext(consumer).count().get();
    }

    default Stream<T> println() {
        return doOnNext(System.out::println);
    }

    default Single<Long> count() {
        return new Count(this);
    }

    default Stream<T> prepend(T value) {
        return new PrependOne<T>(value, this);
    }

    default Stream<T> prepend(T[] values) {
        return new Concat<T>(create(Iterables.fromArray(values)), this);
    }

    default Stream<T> prepend(StreamIterable<? extends T> values) {
        return new Concat<T>(values, this);
    }
    
    default Stream<T> prepend(Iterable<? extends T> values) {
        return prepend(Stream.from(values));
    }

    default Stream<T> concatWith(StreamIterable<? extends T> values) {
        return new Concat<T>(this, values);
    }
    
    default Stream<T> concatWith(Iterable<? extends T> values) {
        return concatWith(Stream.from(values));
    }

    default <R> Stream<R> flatMap(Function<? super T, ? extends StreamIterable<? extends R>> function) {
        return new FlatMap<T, R>(function, this);
    }
    
    default <R> Stream<R> flatMap(BiConsumer<? super T, ? super Consumer<R>> generator,
            Consumer<? super Consumer<R>> onFinish) {
        return new FlatMapGenerator<T, R>(generator, onFinish, this);
    }

    /**
     * Using a consumer to report items to downstream is more performant (fewer
     * allocations) because a Stream object doesn't have to be created for each
     * upstream element.
     * 
     * @param <R>       type of return stream
     * @param generator generator of downstream items
     * @return new stream
     */
    default <R> Stream<R> flatMap(BiConsumer<? super T, ? super Consumer<R>> generator) {
        return new FlatMapGenerator<T, R>(generator, Consumers.doNothing(), this);
    }

    default <R> Stream<R> flatMapJavaStream(
            Function<? super T, ? extends java.util.stream.Stream<? extends R>> function) {
        return flatMap(x -> Stream.from(function.apply(x)));
    }

    default Maybe<T> findFirst(Predicate<? super T> predicate) {
        if (predicate == Predicates.alwaysFalse()) {
            return Maybe.empty();
        } else {
            return filter(predicate).first();
        }
    }

    default Maybe<T> first() {
        return new First<T>(this);
    }

    default Stream<T> doOnNext(Consumer<? super T> consumer) {
        return new DoOnNext<T>(consumer, this);
    }

    default Stream<T> doWithIndex(BiConsumer<? super Long, ? super T> consumer) {
        return defer(() -> {
            long[] idx = new long[1];
            return doOnNext(x -> {
                consumer.accept(idx[0], x);
                idx[0]++;
            });
        });
    }

    default Stream<T> doOnError(Consumer<? super Throwable> consumer) {
        return new DoOnError<T>(consumer, this);
    }

    default Stream<T> doOnComplete(Action action) {
        return new DoOnComplete<T>(action, this);
    }

    default Stream<T> doOnDispose(Action action) {
        return doBeforeDispose(action);
    }

    default Stream<T> doBeforeDispose(Action action) {
        return new DoOnDispose<T>(action, this, true);
    }

    default Stream<T> doAfterDispose(Action action) {
        return new DoOnDispose<T>(action, this, false);
    }

    default Stream<T> doOnEmpty(Action action) {
        return new DoOnEmpty<T>(this, action);
    }
    
    default Stream<T> doOnComplete(Consumer<? super Long> countAction) {
        return defer(() -> {
            long[] count = new long[1];
            return this //
                    .doOnNext(t -> count[0]++) //
                    .doOnComplete(() -> countAction.accept(count[0]));
        });
    }

    default Maybe<T> last() {
        return new Last<T>(this);
    }

    default Maybe<T> get(long index) {
        return take(index + 1).last();
    }

    default Stream<T> take(long n) {
        return new Take<T>(n, this);
    }
    
    default Stream<T> limit(long n) {
        return take(n);
    }

    default Stream<T> takeLast(long n) {
        return new TakeLast<T>(this, n);
    }

    default <R> Stream<R> transform(Function<? super Stream<T>, ? extends Stream<? extends R>> transformer) {
        return new Transform<T, R>(transformer, this);
    }

    default <R> Single<R> transformSingle(Function<? super Stream<T>, ? extends Single<? extends R>> transformer) {
        return new TransformSingle<T, R>(transformer, this);
    }

    default <R> Maybe<R> transformMaybe(Function<? super Stream<T>, ? extends Maybe<? extends R>> transformer) {
        return new TransformMaybe<T, R>(transformer, this);
    }

    default <R> Stream<R> compose(Function<? super Stream<T>, ? extends Stream<? extends R>> transformer) {
        return transform(transformer);
    }

    default <R> Single<R> composeSingle(Function<? super Stream<T>, ? extends Single<? extends R>> transformer) {
        return transformSingle(transformer);
    }

    default <R> Maybe<R> composeMaybe(Function<? super Stream<T>, ? extends Maybe<? extends R>> transformer) {
        return transformMaybe(transformer);
    }

    default Stream<T> switchOnError(Function<? super Throwable, ? extends Stream<? extends T>> function) {
        return new SwitchOnError<T>(function, this);
    }

    default Stream<T> switchOnEmpty(Callable<? extends Stream<T>> factory) {
        return new SwitchOnEmpty<T>(this, factory);
    }

    default <R, S> Stream<S> zipWith(Stream<? extends R> stream, BiFunction<T, R, S> combiner) {
        return new Zip<R, S, T>(this, stream, combiner);
    }

    default java.util.stream.Stream<T> toStreamJava() {
        StreamIterator<T> si = iterator();
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(si, 0);
        return StreamSupport.stream(spliterator, false).onClose(() -> si.dispose());
    }

    default <K, V> Single<java.util.Map<K, V>> toMap(Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction) {
        return collect(HashMap::new, (BiConsumer<java.util.Map<K, V>, T>) (m, item) -> m.put(keyFunction.apply(item),
                valueFunction.apply(item)));
    }

    default Single<String> join(String delimiter) {
        return collect(() -> new StringBuilder(), (b, x) -> {
            if (b.length() > 0) {
                b.append(delimiter);
            }
            b.append(x);
        }).map(b -> b.toString());
    }

    default Stream<String> split(String delimiter) {
        return new Split(delimiter, this);
    }

    default Tester<T> test() {
        return new Tester<T>(this);
    }

    default Stream<List<T>> buffer(int size) {
        return buffer(size, size);
    }

    default Stream<List<T>> buffer(int size, int step) {
        return new Buffer<T>(this, size, step, true);
    }

    /**
     * Buffers the stream into list chunks of given size and step. If and only if
     * copy is set to false the actual buffer used internally will be emitted. This
     * is a performance-oriented offering to reduce allocation pressure but has
     * side-effects. You must consume the emitted list immediately because the next
     * emitted buffer reuses that object.
     * 
     * @param size buffer size
     * @param step buffer step
     * @param copy if false then the internal buffer will be emitted (but must be
     *             consumed immediately)
     * @return stream of lists
     */
    default Stream<List<T>> buffer(int size, int step, boolean copy) {
        return new Buffer<T>(this, size, step, copy);
    }

    default Stream<T> skip(long size) {
        return new Skip<T>(size, this);
    }
    
    default Stream<T> skipLast(int size) {
        Preconditions.checkArgument(size >=0, "size must be non-negative");
        return new SkipLast<T>(size, this);
    }

    default Stream<T> skipUntil(Predicate<? super T> predicate) {
        return new SkipUntil<T>(this, predicate, false);
    }

    default Stream<T> skipWhile(Predicate<? super T> predicate) {
        return new SkipUntil<T>(this, predicate, true);
    }

    default Stream<T> repeat(long count) {
        return new Repeat<T>(count, this);
    }

    default Stream<T> repeat() {
        return repeat(Long.MAX_VALUE);
    }

    default Stream<T> takeUntil(Predicate<? super T> predicate) {
        return new TakeWithPredicate<T>(predicate, this, true);
    }

    default Stream<T> takeWhile(Predicate<? super T> predicate) {
        return new TakeWithPredicate<T>(predicate, this, false);
    }

    default Stream<List<T>> bufferUntil(BiPredicate<? super List<T>, ? super T> condition, boolean emitRemainder) {
        return new BufferWithPredicate<T>(condition, emitRemainder, true, this);
    }

    default Stream<List<T>> bufferWhile(BiPredicate<? super List<T>, ? super T> condition, boolean emitRemainder) {
        return new BufferWithPredicate<T>(condition, emitRemainder, false, this);
    }

    default Stream<List<T>> bufferUntil(BiPredicate<? super List<T>, ? super T> condition, boolean emitRemainder,
            int step, int maxReplay) {
        return bufferUntil(condition, emitRemainder, list -> step, maxReplay);
    }

    default Stream<List<T>> bufferWhile(BiPredicate<? super List<T>, ? super T> condition, boolean emitRemainder,
            int step, int maxReplay) {
        return bufferWhile(condition, emitRemainder, list -> step, maxReplay);
    }
    
    default Stream<List<T>> bufferUntil(BiPredicate<? super List<T>, ? super T> condition, boolean emitRemainder,
            Function<? super List<T>, Integer> step, int maxReplay) {
        return bufferUntil(ArrayList::new, StreamUtils.listAccumulator(), condition, emitRemainder, step, maxReplay);
    }
    
    default Stream<List<T>> bufferWhile(BiPredicate<? super List<T>, ? super T> condition, boolean emitRemainder,
            Function<? super List<T>, Integer> step, int maxReplay) {
        return bufferWhile(ArrayList::new, StreamUtils.listAccumulator(), condition, emitRemainder, step, maxReplay);
    }

    default <S> Stream<S> bufferUntil(Callable<? extends S> factory,
            BiFunction<? super S, ? super T, ? extends S> accumulator, BiPredicate<? super S, ? super T> condition,
            boolean emitRemainder, Function<? super S, Integer> step, int maxReplay) {
        return new BufferWithFactoryPredicateAndStep<>(factory, accumulator, condition, emitRemainder, true, this,
                step, maxReplay);
    }
    
    default <S> Stream<S> bufferWhile(Callable<? extends S> factory,
            BiFunction<? super S, ? super T, ? extends S> accumulator, BiPredicate<? super S, ? super T> condition,
            boolean emitRemainder, Function<? super S, Integer> step, int maxReplay) {
        return new BufferWithFactoryPredicateAndStep<>(factory, accumulator, condition, emitRemainder, false, this,
                step, maxReplay);
    }

    default Stream<Indexed<T>> mapWithIndex(long startIndex) {
        return defer(() -> {
            long[] index = new long[] { startIndex };
            return map(x -> {
                long n = index[0];
                index[0] = n + 1;
                return Indexed.create(x, n);
            });
        });
    }

    default Stream<Indexed<T>> mapWithIndex() {
        return mapWithIndex(0);
    }

    default Stream<T> cache() {
        return new Cache<T>(this);
    }

    default Stream<T> every(long n, BiConsumer<Long, T> action) {
        return defer(() -> {
            long[] index = new long[1];
            return doOnNext(x -> {
                index[0]++;
                if (index[0] % n == 0) {
                    action.accept(index[0], x);
                }
            });
        });
    }

    default Stream<T> ignoreDisposalError(Consumer<? super Throwable> action) {
        Preconditions.checkNotNull(action);
        return new IgnoreDisposalError<T>(this, action);
    }

    default Stream<T> ignoreDisposalError() {
        return new IgnoreDisposalError<T>(this, null);
    }

    default Maybe<T> max(Comparator<? super T> comparator) {
        return new Max<T>(this, comparator, false);
    }

    default Maybe<T> min(Comparator<? super T> comparator) {
        return new Max<T>(this, comparator, true);
    }

    default Single<Boolean> all(Predicate<? super T> predicate) {
        return new All<T>(this, predicate);
    }

    default Single<Boolean> any(Predicate<? super T> predicate) {
        return new Any<T>(this, predicate);
    }

    @SuppressWarnings("unchecked")
    default <R> Stream<R> cast(Class<R> cls) {
        Preconditions.checkNotNull(cls);
        return (Stream<R>) (Stream<Object>) this;
    }

    default Single<Boolean> contains(T value) {
        Preconditions.checkNotNull(value);
        return any(x -> value.equals(x));
    }

    default Stream<T> distinctUntilChanged() {
        return distinctUntilChanged(Function.identity());
    }

    default <K> Stream<T> distinct(Function<? super T, K> keySelector) {
        return new Distinct<T, K>(this, keySelector);
    }

    default Stream<T> distinct() {
        return distinct(Function.identity());
    }

    default <K> Stream<T> distinctUntilChanged(Function<? super T, K> keySelector) {
        return new DistinctUntilChanged<T, K>(this, keySelector);
    }

    default Maybe<T> maybe() {
        return new ToMaybe<T>(this);
    }

    default <R> R to(Function<? super Stream<T>, R> mapper) {
        return mapper.applyUnchecked(this);
    }

    default Stream<T> printStackTrace() {
        return doOnError(Throwable::printStackTrace);
    }

    default Stream<Notification<T>> materialize() {
        return new Materialize<T>(this);
    }

    default <R> Stream<R> dematerialize(Function<? super T, Notification<? extends R>> function) {
        return new Dematerialize<T, R>(this, function);
    }

    default Stream<T> reverse() {
        return new Reverse<T>(this);
    }

    default Stream<T> doOnStart(Action action) {
        return new DoOnStart<T>(this, action);
    }

    default Stream<T> delayStart(long duration, TimeUnit unit) {
        return doOnStart(() -> unit.sleep(duration));
    }

    default Stream<T> mergeWith(Stream<? extends T> stream) {
        return merge(this, stream);
    }

    default Stream<T> retryWhen(Function<? super Throwable, ? extends Single<?>> function) {
        return new RetryWhen<T>(this, function);
    }

    default RetryWhenBuilder<T> retryWhen() {
        return new RetryWhenBuilder<T>(this);
    }

    default Stream<T> repeatLast(long count) {
        if (count == 0) {
            return this;
        } else {
            return new RepeatLast<T>(this, count);
        }
    }

    default <R> Stream<R> scan(R initialValue, BiFunction<? super R, ? super T, ? extends R> accumulator) {
        return Stream.defer(new Callable<Stream<R>>() {

            R r = initialValue;

            @Override
            public Stream<R> call() throws Exception {
                return Stream.this.map(x -> {
                    r = accumulator.apply(r, x);
                    return r;
                });
            }
        });
    }

    default <R extends Number> Single<Statistics> statistics(Function<? super T, ? extends R> mapper) {
        return statistics(this.map(mapper));
    }

    default Stream<T> repeatLast() {
        return repeatLast(Long.MAX_VALUE);
    }
    
    default Publisher<T> publish() {
        return new Publisher<T>(this);
    }

    static Stream<Set<Integer>> powerSet(int n) {
        return new PowerSet(n);
    }

    static <T> Stream<List<Integer>> permutations(int size) {
        List<Integer> indexes = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
            indexes.add(i);
        }
        return Stream.from(Permutations.iterable(indexes)) //
                .scan(indexes, (a, swap) -> {
                    List<Integer> b = new ArrayList<Integer>(a);
                    b.set(swap.left(), a.get(swap.right()));
                    b.set(swap.right(), a.get(swap.left()));
                    return b;
                }) //
                .prepend(new ArrayList<>(indexes));
    }

    static <T extends Number> Single<Statistics> statistics(Stream<T> stream) {
        // don't need to use factory to create Statistics instance because is immutable
        return stream.reduce(new Statistics(), //
                (stats, x) -> stats.add(x.doubleValue()));
    }

}
