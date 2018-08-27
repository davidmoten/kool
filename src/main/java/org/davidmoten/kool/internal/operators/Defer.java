package org.davidmoten.kool.internal.operators;

import java.util.Iterator;
import java.util.function.Supplier;

public class Defer<T> implements Iterable<T> {

    private final Supplier<? extends Iterable<? extends T>> supplier;

    public Defer(Supplier<? extends Iterable<? extends T>> supplier) {
        this.supplier = supplier;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<T> iterator() {
        return (Iterator<T>) supplier.get().iterator();
    }

}
