package org.davidmoten.kool;

import java.util.Optional;

public final class MaybeTester<T> {

    private Optional<T> value;
    private Throwable error;

    public MaybeTester(Maybe<T> maybe) {
        try {
            value = maybe.get();
        } catch (Throwable e) {
            error = e;
        }
    }

    public void assertValue(T t) {
        assertNoError();
        if (!value.isPresent()) {
            throw new AssertionError("Value " + t + " expected but no value found");
        } else if (!value.get().equals(t)) {
            throw new AssertionError("Value " + t + " expected but found " + value.get());
        }
    }

    public void assertNoValue() {
        assertNoError();
        if (value.isPresent()) {
            throw new AssertionError("Did not expect a value but found " + value.get());
        }
    }

    public void assertNoError() {
        if (error != null) {
            throw new AssertionError("no error expected but one was found", error);
        }
    }

    public void assertError(Class<? extends Throwable> cls) {
        if (error == null) {
            throw new AssertionError("error expected of type " + cls + " but no error was found");
        } else if (!cls.isInstance(error)) {
            throw new AssertionError("error expected of type " + cls + " but was of type " + error.getClass());
        }
    }

    public void assertErrorMessage(String message) {
        if (error == null) {
            throw new AssertionError("error expected but no error was found");
        } else if (!message.equals(error.getMessage())) {
            throw new AssertionError("error expected with msg " + message + " but message was: " + error.getMessage());
        }
    }

}
