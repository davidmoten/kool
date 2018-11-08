package org.davidmoten.kool;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.davidmoten.kool.exceptions.UncheckedException;
import org.davidmoten.kool.internal.operators.stream.Buffer;
import org.davidmoten.kool.internal.operators.stream.BufferWithPredicate;
import org.davidmoten.kool.internal.operators.stream.Collect;
import org.davidmoten.kool.internal.operators.stream.Concat;
import org.davidmoten.kool.internal.operators.stream.Count;
import org.davidmoten.kool.internal.operators.stream.Defer;
import org.davidmoten.kool.internal.operators.stream.DoOnComplete;
import org.davidmoten.kool.internal.operators.stream.DoOnDispose;
import org.davidmoten.kool.internal.operators.stream.DoOnError;
import org.davidmoten.kool.internal.operators.stream.DoOnNext;
import org.davidmoten.kool.internal.operators.stream.Filter;
import org.davidmoten.kool.internal.operators.stream.First;
import org.davidmoten.kool.internal.operators.stream.FlatMap;
import org.davidmoten.kool.internal.operators.stream.FromBufferedReader;
import org.davidmoten.kool.internal.operators.stream.IsEmpty;
import org.davidmoten.kool.internal.operators.stream.Last;
import org.davidmoten.kool.internal.operators.stream.Map;
import org.davidmoten.kool.internal.operators.stream.PrependOne;
import org.davidmoten.kool.internal.operators.stream.Range;
import org.davidmoten.kool.internal.operators.stream.ReduceNoInitialValue;
import org.davidmoten.kool.internal.operators.stream.ReduceWithInitialValueSupplier;
import org.davidmoten.kool.internal.operators.stream.Repeat;
import org.davidmoten.kool.internal.operators.stream.RepeatElement;
import org.davidmoten.kool.internal.operators.stream.Skip;
import org.davidmoten.kool.internal.operators.stream.Sorted;
import org.davidmoten.kool.internal.operators.stream.Split;
import org.davidmoten.kool.internal.operators.stream.SwitchOnError;
import org.davidmoten.kool.internal.operators.stream.Take;
import org.davidmoten.kool.internal.operators.stream.TakeWithPredicate;
import org.davidmoten.kool.internal.operators.stream.ToSingle;
import org.davidmoten.kool.internal.operators.stream.Transform;
import org.davidmoten.kool.internal.operators.stream.Using;
import org.davidmoten.kool.internal.operators.stream.Zip;
import org.davidmoten.kool.internal.util.Iterables;
import org.davidmoten.kool.internal.util.StreamImpl;
import org.davidmoten.kool.internal.util.StreamUtils;

public interface Stream<T> extends StreamIterable<T> {

    public static final int DEFAULT_BUFFER_SIZE = 16;

    static <T> Stream<T> create(StreamIterable<T> source) {
        return new StreamImpl<T>(source);
    }

    static <T> Stream<T> create(Iterable<T> source) {
        return new StreamImpl<T>(source);
    }

    public static <T> Stream<T> of(T t) {
        return create(Collections.singleton(t));
    }

    public static <T> Stream<T> of(T t1, T t2) {
        return create(Iterables.ofNoCopy(t1, t2));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3) {
        return create(Iterables.ofNoCopy(t1, t2, t3));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5, t6));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5, t6, t7));
    }

    public static <T> Stream<T> of(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8) {
        return create(Iterables.ofNoCopy(t1, t2, t3, t4, t5, t6, t7, t8));
    }

    public static <T> Stream<T> error(Throwable e) {
        return Stream.fromIterable(new StreamIterable<T>() {
            @Override
            public StreamIterator<T> iterator() {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else if (e instanceof Error) {
                    throw (Error) e;
                } else {
                    throw new UncheckedException(e);
                }
            }
        });
    }

    public static <T> Stream<T> error(Supplier<Throwable> supplier) {
        return Stream.fromIterable(new StreamIterable<T>() {
            @Override
            public StreamIterator<T> iterator() {
                Throwable e = supplier.get();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else if (e instanceof Error) {
                    throw (Error) e;
                } else {
                    throw new UncheckedException(e);
                }
            }
        });
    }

    public static <T> Stream<T> fromIterable(Iterable<T> iterable) {
        return create(iterable);
    }

    public static <T> Stream<T> fromCallable(Callable<? extends T> callable) {
        return defer(() -> {
            try {
                return Stream.of(callable.call());
            } catch (Exception e) {
                return Stream.error(e);
            }
        });
    }

    public static Stream<String> lines(BufferedReader reader) {
        return lines(() -> reader);
    }

    public static Stream<String> lines(Supplier<BufferedReader> readerFactory) {
        return new FromBufferedReader(readerFactory);
    }

    public static Stream<String> lines(Supplier<InputStream> inFactory, Charset charset) {
        return lines(() -> new BufferedReader(new InputStreamReader(inFactory.get(), charset)));
    }

    public static Stream<String> lines(File file, Charset charset) {
        return Stream.using(() -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        }, in -> lines(() -> in, charset));
    }

    public static Stream<String> lines(File file) {
        return lines(file, StandardCharsets.UTF_8);
    }

    public static Stream<String> linesFromResource(String resource, Charset charset) {
        return linesFromResource(Stream.class, resource, charset);
    }

    public static Stream<String> linesFromResource(Class<?> cls, String resource, Charset charset) {
        return lines(() -> cls.getResourceAsStream(resource), charset);
    }

    public static Stream<String> linesFromResource(String resource) {
        return linesFromResource(Stream.class, resource, StandardCharsets.UTF_8);
    }

    public static Stream<Long> range(long start, long length) {
        return create(new Range(start, length));
    }

    public static Stream<Long> ordinals() {
        return range(1, Long.MAX_VALUE);
    }

    public static <T> Stream<T> defer(Supplier<? extends Stream<? extends T>> supplier) {
        return new Defer<T>(supplier);
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<T> empty() {
        return (Stream<T>) StreamUtils.EmptyHolder.EMPTY;
    }

    public static <R, T> Stream<T> using(Supplier<R> resourceFactory,
            Function<? super R, ? extends Stream<? extends T>> streamFactory, Consumer<? super R> closer) {
        return new Using<R, T>(resourceFactory, streamFactory, closer);
    }

    public static <R extends Closeable, T> Stream<T> using(Supplier<R> resourceFactory,
            Function<? super R, ? extends Stream<? extends T>> streamFactory) {
        return new Using<R, T>(resourceFactory, streamFactory, CLOSEABLE_CLOSER);
    }

    static final Consumer<Closeable> CLOSEABLE_CLOSER = new Consumer<Closeable>() {

        @Override
        public void accept(Closeable c) {
            try {
                c.close();
            } catch (IOException e) {
                Plugins.onError(e);
            }
        }

    };

    public static <T> Stream<T> repeatElement(T t) {
        return repeatElement(t, Long.MAX_VALUE);
    }

    public static <T> Stream<T> repeatElement(T t, long count) {
        return new RepeatElement<T>(t, count);
    }

    public default Single<Boolean> isEmpty() {
        return new IsEmpty(this);
    }

    public default Single<Boolean> hasElements() {
        return isEmpty().map(x -> !x);
    }

    public default Stream<T> sorted(Comparator<? super T> comparator) {
        return new Sorted<T>(comparator, this);
    }

    @SuppressWarnings("unchecked")
    public default Stream<T> sorted() {
        return (Stream<T>) ((Stream<Comparable<Object>>) this).sorted(Comparator.naturalOrder());
    }

    public default <R> Stream<R> map(Function<? super T, ? extends R> function) {
        return new Map<T, R>(function, this);
    }

    public default Maybe<T> reduce(BiFunction<? super T, ? super T, ? extends T> reducer) {
        return new ReduceNoInitialValue<T>(reducer, this).iterator().next();
    }

    public default <R> R reduceWithInitialValue(R initialValue, BiFunction<? super R, ? super T, ? extends R> reducer) {
        return reduce(() -> initialValue, reducer);
    }

    public default <R> R reduce(Supplier<R> initialValueFactory,
            BiFunction<? super R, ? super T, ? extends R> reducer) {
        return new ReduceWithInitialValueSupplier<R, T>(initialValueFactory, reducer, this).iterator().next();
    }

    public default <R> Single<R> collect(Supplier<? extends R> factory, BiConsumer<? super R, ? super T> collector) {
        return new Collect<T, R>(factory, collector, this).single();
    }

    public default Single<T> single() {
        return new ToSingle<T>(this);
    }

    public default List<T> toList() {
        return toList(DEFAULT_BUFFER_SIZE);
    }

    public default Set<T> toSet() {
        return toSet(DEFAULT_BUFFER_SIZE);
    }

    public default Set<T> toSet(int sizeHint) {
        return Iterables.addAll(new HashSet<T>(sizeHint), this);
    }

    public default List<T> toList(int sizeHint) {
        return Iterables.addAll(new ArrayList<T>(sizeHint), this);
    }

    public default Stream<T> filter(Predicate<? super T> function) {
        if (function == Predicates.alwaysTrue()) {
            return this;
        } else {
            return new Filter<T>(function, this);
        }
    }

    public default void forEach() {
        count().get();
    }
    
    public default Stream<T> println() {
        return doOnNext(System.out::println);
    }

    public default Single<Long> count() {
        return new Count(this);
    }

    public default Stream<T> prepend(T value) {
        return new PrependOne<T>(value, this);
    }

    public default Stream<T> prepend(T[] values) {
        return new Concat<T>(create(Iterables.fromArray(values)), this);
    }

    public default Stream<T> prepend(StreamIterable<? extends T> values) {
        return new Concat<T>(values, this);
    }

    public default Stream<T> concatWith(StreamIterable<? extends T> values) {
        return new Concat<T>(this, values);
    }

    public default <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> function) {
        return new FlatMap<T, R>(function, this);
    }

    public default Maybe<T> findFirst(Predicate<? super T> predicate) {
        if (predicate == Predicates.alwaysFalse()) {
            return Maybe.empty();
        } else {
            return filter(predicate).first();
        }
    }

    public default Maybe<T> first() {
        return Iterables.first(new First<T>(this).iterator());
    }

    public default Stream<T> doOnNext(Consumer<? super T> consumer) {
        return new DoOnNext<T>(consumer, this);
    }

    public default Stream<T> doOnError(Consumer<? super Throwable> consumer) {
        return new DoOnError<T>(consumer, this);
    }

    public default Stream<T> doOnComplete(Runnable action) {
        return new DoOnComplete<T>(action, this);
    }

    public default Stream<T> doOnDispose(Runnable action) {
        return doBeforeDispose(action);
    }

    public default Stream<T> doBeforeDispose(Runnable action) {
        return new DoOnDispose<T>(action, this, true);
    }

    public default Stream<T> doAfterDispose(Runnable action) {
        return new DoOnDispose<T>(action, this, false);
    }

    public default Maybe<T> last() {
        return Iterables.first(new Last<T>(this).iterator());
    }

    public default Maybe<T> get(int index) {
        return take(index + 1).last();
    }

    public default Stream<T> take(long n) {
        return new Take<T>(n, this);
    }

    public default <R> Stream<R> transform(Function<? super Stream<T>, ? extends Stream<? extends R>> transformer) {
        return new Transform<T, R>(transformer, this);
    }

    public default <R> Stream<R> compose(Function<? super Stream<T>, ? extends Stream<? extends R>> transformer) {
        return transform(transformer);
    }

    public default Stream<T> switchOnError(Function<? super Throwable, ? extends Stream<? extends T>> function) {
        return new SwitchOnError<T>(function, this);
    }

    public default <R, S> Stream<S> zipWith(Stream<? extends R> stream, BiFunction<T, R, S> combiner) {
        return new Zip<R, S, T>(this, stream, combiner);
    }

    public default java.util.stream.Stream<T> toStreamJava() {
        // TODO don't use toList
        return toList().stream();
    }

    public default <K, V> Single<java.util.Map<K, V>> toMap(Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction) {
        return collect(HashMap::new, (m, item) -> m.put(keyFunction.apply(item), valueFunction.apply(item)));
    }

    public default Single<String> join(String delimiter) {
        return collect(() -> new StringBuilder(), (b, x) -> {
            if (b.length() > 0) {
                b.append(delimiter);
            }
            b.append(x);
        }).map(b -> b.toString());
    }

    public default Stream<String> split(String delimiter) {
        return new Split(delimiter, this);
    }

    public default Tester<T> test() {
        return new Tester<T>(this);
    }

    public default Stream<List<T>> buffer(int size) {
        return new Buffer<T>(size, this);
    }

    public default Stream<T> skip(int size) {
        return new Skip<T>(size, this);
    }

    public default Stream<T> repeat(long count) {
        return new Repeat<T>(count, this);
    }

    public default Stream<T> repeat() {
        return repeat(Long.MAX_VALUE);
    }

    public default Stream<T> takeUntil(Predicate<? super T> predicate) {
        return new TakeWithPredicate<T>(predicate, this, true);
    }

    public default Stream<T> takeWhile(Predicate<? super T> predicate) {
        return new TakeWithPredicate<T>(predicate, this, false);
    }

    public default Stream<List<T>> bufferWhile(BiPredicate<? super List<T>, ? super T> condition,
            boolean emitRemainder) {
        return new BufferWithPredicate<T>(condition, emitRemainder, false, this);
    }

    public default Stream<List<T>> bufferUntil(BiPredicate<? super List<T>, ? super T> condition,
            boolean emitRemainder) {
        return new BufferWithPredicate<T>(condition, emitRemainder, true, this);
    }

    public default Stream<Indexed<T>> mapWithIndex() {
        return defer(() -> {
            int[] index = new int[1];
            return map(x -> {
                int n = index[0];
                index[0]=n+1;
                return Indexed.create(x, n);
            });
        });
    }

    // TODO
    // toStreamJava ,
    // skipUntil, skipWhile, retryWhen, cache,
    // doOnEmpty, switchIfEmpty, interleaveWith, materialize
    // add Single.flatMapMaybe, Maybe.flatMapSingle, Maybe.flatMapMaybe

}
