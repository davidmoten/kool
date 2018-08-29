package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tester<T> {

    private final List<T> list;
    private Throwable error;

    public Tester(Stream<T> stream) {
        list = new ArrayList<T>();
        try {
            stream.forEach(x -> list.add(x));
            this.error = null;
        } catch (Throwable e) {
            this.error = e;
        }
    }
    
    public Tester<T> assertValues(T... expected) {
        if (!Arrays.asList(expected).equals(list)) {
            throw new AssertionError("values not equal: expected=" + expected + ", found=" + list);
        }
        return this;
    }

}
