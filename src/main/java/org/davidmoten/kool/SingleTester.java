package org.davidmoten.kool;

public final class SingleTester<T> {

    private final Single<T> single;

    SingleTester(Single<T> single) {
        this.single = single;
    }
    
    public void assertValue(T value) {
        T v = single.get();
        if (!value.equals(v)) {
            throw new AssertionError("single result "+ v + " not equal to " + value);
        }
    }
}
