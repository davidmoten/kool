package org.davidmoten.kool;

import com.github.davidmoten.guavamini.Preconditions;

public final class SingleTester<T> {

    private T value;
    private Throwable error;

    SingleTester(Single<T> single) {
        try {
            this.value = single.get();
        } catch (Throwable e) {
            this.error = e;
        }
    }

    public SingleTester<T> assertValue(T v) {
        assertNoError();
        if (!v.equals(value)) {
            throw new AssertionError("expected " + value + " but value is " + v);
        }
        return this;
    }

    public SingleTester<T> assertValueOnly(T value) {
        return assertValue(value);
    }

    public SingleTester<T> assertNoError() {
        if (error != null) {
            throw new AssertionError("no error expected but one found", error);
        }
        return this;
    }

    public SingleTester<T> assertError(Class<? extends Throwable> cls) {
        Preconditions.checkNotNull(cls, "cls cannot be null");
        if (!cls.isInstance(error)) {
            throw new AssertionError("error expected of type " + cls + " but was of type " + error.getClass());
        }
        return this;
    }

    public SingleTester<T> assertErrorMessage(String message) {
        Preconditions.checkNotNull(message, "message cannot be null");
        if (!message.equals(error.getMessage())) {
            throw new AssertionError("error expected with msg " + message + " but message was: " + error.getMessage());
        }
        return this;
    }
}
