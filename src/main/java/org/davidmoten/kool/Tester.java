package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public final class Tester<T> {

    private final List<T> list;
    private Throwable error;

    public Tester(Stream<T> stream) {
        list = new ArrayList<T>();
        try {
            stream.forEach(list::add);
            this.error = null;
        } catch (Throwable e) {
            this.error = e;
        }
    }

    @SafeVarargs
    public final Tester<T> assertValues(T... expected) {
        if (!Arrays.asList(expected).equals(list)) {
            throw new AssertionError("values not equal: expected=" + Arrays.toString(expected) + ", found=" + list);
        }
        return this;
    }

    public Tester<T> assertNoValues() {
        if (!list.isEmpty()) {
            throw new AssertionError("values not empty: " + list);
        }
        return this;
    }

    public Tester<T> assertNoValuesOnly() {
        assertNoError();
        assertNoValues();
        return this;
    }

    @SafeVarargs
    public final Tester<T> assertValuesOnly(T... expected) {
        assertNoError();
        assertValues(expected);
        return this;
    }

    public Tester<T> assertNoError() {
        if (error != null) {
            throw new AssertionError(error);
        }
        return this;
    }

    public Tester<T> assertError(Class<? extends Throwable> cls) {
        if (error == null) {
            throw new AssertionError("no error thrown");
        } else if (!error.getClass().isAssignableFrom(cls)) {
            throw new AssertionError("error " + error.getClass() + " is not an instance of " + cls);
        }
        return this;
    }
    
    public Tester<T> assertError(Predicate<? super Throwable> predicate) {
        if (error == null) {
            throw new AssertionError("no error thrown");
        } else if (!predicate.test(error)) {
            error.printStackTrace();
            throw new AssertionError("error " + error.getClass() + " failed predicate");
        }
        return this;
    }

}
