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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((error == null) ? 0 : error.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Notification<?> other = (Notification<?>) obj;
        if (error == null) {
            if (other.error != null)
                return false;
        } else if (!error.equals(other.error))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
