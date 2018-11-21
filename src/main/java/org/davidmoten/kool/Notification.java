package org.davidmoten.kool;

import com.github.davidmoten.guavamini.Preconditions;

public class Notification<T> {

    private final Throwable error;
    private final T value;

    private static final Notification<Object> COMPLETE = new Notification<>(null, null);

    Notification(Throwable error, T value) {
        this.error = error;
        this.value = value;
    }

    public static <T> Notification<T> of(T value) {
        return new Notification<T>(null, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> Notification<T> complete() {
        return (Notification<T>) COMPLETE;
    }

    public static <T> Notification<T> error(Throwable error) {
        return new Notification<T>(error, null);
    }

    public T value() {
        return Preconditions.checkNotNull(value);
    }

    public boolean hasValue() {
        return value != null;
    }

    public boolean isComplete() {
        return error == null && value == null;
    }

    public boolean isError() {
        return error != null;
    }

    public Throwable error() {
        return error;
    }

}
