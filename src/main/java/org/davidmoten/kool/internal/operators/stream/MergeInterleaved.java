package org.davidmoten.kool.internal.operators.stream;

import java.util.List;
import java.util.NoSuchElementException;

import org.davidmoten.kool.Stream;
import org.davidmoten.kool.StreamIterator;
import org.davidmoten.kool.exceptions.CompositeException;
import org.davidmoten.kool.internal.util.Exceptions;

import com.github.davidmoten.guavamini.Lists;

public final class MergeInterleaved<T> implements Stream<T> {

    private final Stream<? extends T>[] streams;

    @SafeVarargs
    public MergeInterleaved(Stream<? extends T>... streams) {
        this.streams = streams;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new StreamIterator<T>() {

            List<StreamIterator<? extends T>> list = getIterators(streams);
            int index = 0;
            T next;

            @Override
            public boolean hasNext() {
                load();
                return next != null;
            }

            @Override
            public T next() {
                load();
                T v = next;
                if (v == null) {
                    throw new NoSuchElementException();
                } else {
                    next = null;
                    return v;
                }
            }

            @Override
            public void dispose() {
                if (list != null) {
                    try {
                        List<Throwable> errors = getDisposalErrors();
                        throw new CompositeException(errors);
                    } finally {
                        list = null;
                    }
                }
            }

            private List<Throwable> getDisposalErrors() {
                List<Throwable> errors = Lists.newArrayList();
                for (StreamIterator<? extends T> it : list) {
                    try {
                        it.dispose();
                    } catch (Throwable e) {
                        errors.add(e);
                    }
                }
                return errors;
            }

            private void load() {
                if (list != null && !list.isEmpty() && next == null) {
                    while (true) {
                        StreamIterator<? extends T> it = list.get(index);
                        if (it.hasNext()) {
                            next = it.nextNullChecked();
                            index = (index + 1) % list.size();
                            break;
                        } else {
                            int idx = list.indexOf(it);
                            list.remove(idx);
                            try {
                                it.dispose();
                            } catch (Throwable e) {
                                // dispose all and throw
                                List<Throwable> errors = getDisposalErrors();
                                if (errors.isEmpty()) {
                                    Exceptions.rethrow(e);
                                    return;
                                } else {
                                    errors.add(e);
                                    throw new CompositeException(errors);
                                }
                            }
                            if (list.isEmpty()) {
                                list = null;
                                break;
                            }
                            if (index >= idx) {
                                index = index % list.size();
                            }
                        }
                    }
                }
            }

        };
    }

    static <T> List<StreamIterator<? extends T>> getIterators(Stream<? extends T>[] streams) {
        List<StreamIterator<? extends T>> list = Lists.newArrayList();
        for (Stream<? extends T> stream : streams) {
            list.add(stream.iteratorNullChecked());
        }
        return list;
    }

}
